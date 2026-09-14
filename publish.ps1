<#
.SYNOPSIS
    Builds FlyingAllowed and publishes the shaded jar to Modrinth.

.DESCRIPTION
    UPDATES.md drives the release: its first heading is the version being
    published and the lines beneath it are the changelog. gradle.properties is
    updated to match, so the jar and the Modrinth version never disagree. Game
    versions are resolved live from Modrinth's tag API (every release from
    MinGameVersion up), so the supported list does not go stale in this file.

    Publishing is deliberately opt-in: without -Confirm the script performs a
    dry run and shows exactly what it would upload.

.PARAMETER Confirm
    Actually upload. Without this, nothing leaves the machine.

.PARAMETER SkipBuild
    Publish the existing jar instead of rebuilding.

.PARAMETER VersionType
    release (default), beta, or alpha.

.EXAMPLE
    .\publish.ps1
    Dry run: build, resolve metadata, print the plan.

.EXAMPLE
    .\publish.ps1 -Confirm
    Build and upload to Modrinth.
#>
[CmdletBinding()]
param(
    [switch] $Confirm,
    [switch] $SkipBuild,
    [ValidateSet('release', 'beta', 'alpha')]
    [string] $VersionType = 'release'
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$RootDir = $PSScriptRoot

# Modrinth project id for FlyingAllowed, mirrored from FlyingAllowed.MODRINTH_ID.
$ProjectId = '6hSbyuAM'
# BukkitOfUtils: a hard depend in plugin.yml, so it is a required dependency here.
$BouProjectId = '6owv5fWs'
$Loaders = @('bukkit', 'folia', 'paper', 'purpur', 'spigot')
# The plugin targets the 1.16.5 API but runs on older servers; this is the
# oldest game version previous releases were published against.
$MinGameVersion = '1.8'
$UserAgent = 'DrakDv-FlyingAllowed-publish/1.0'

function Write-Step { param([string] $Message) Write-Host "==> $Message" -ForegroundColor Cyan }
function Write-Ok   { param([string] $Message) Write-Host "    $Message" -ForegroundColor Green }
function Write-Warn { param([string] $Message) Write-Host "    $Message" -ForegroundColor Yellow }

# UPDATES.md is the release manifest: the first heading is the version, and the
# lines beneath it (up to the next heading, if any) are the changelog.
function Get-Release {
    $updatesPath = Join-Path $RootDir 'UPDATES.md'
    if (-not (Test-Path $updatesPath)) { throw "UPDATES.md not found at $updatesPath" }

    $lines = @(Get-Content -LiteralPath $updatesPath -Encoding UTF8)

    $start = -1
    $version = $null
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^#{1,2}\s+(.+?)\s*$') {
            $version = $Matches[1].Trim().TrimStart('v', 'V')
            $start = $i + 1
            break
        }
    }
    if ($start -lt 0) { throw 'UPDATES.md has no version heading (expected a line like "# 1.17.0").' }
    if ($version -notmatch '^\d+\.\d+') {
        throw "The first heading of UPDATES.md is '$version', which is not a version number."
    }

    $body = [System.Collections.Generic.List[string]]::new()
    for ($i = $start; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^#{1,2}\s+') { break }
        $body.Add($lines[$i])
    }

    $changelog = ($body -join "`n").Trim()
    if ([string]::IsNullOrWhiteSpace($changelog)) {
        throw "The '$version' section of UPDATES.md has no changelog body."
    }

    return [pscustomobject]@{ Version = $version; Changelog = $changelog }
}

# The jar's version comes from gradle.properties via processResources, so it has
# to agree with UPDATES.md or the uploaded file would not match the version it
# is published under.
function Sync-ProjectVersion {
    param([Parameter(Mandatory)] [string] $Version)

    $propsPath = Join-Path $RootDir 'gradle.properties'
    if (-not (Test-Path $propsPath)) { throw "gradle.properties not found at $propsPath" }

    $content = Get-Content -LiteralPath $propsPath -Raw
    if ($content -notmatch '(?m)^\s*version\s*=\s*(.+?)\s*$') {
        throw 'No "version" key found in gradle.properties.'
    }

    $current = $Matches[1]
    if ($current -eq $Version) {
        Write-Ok "gradle.properties already at $Version."
        return
    }

    $updated = [regex]::Replace($content, '(?m)^(\s*version\s*=\s*).+?\s*$', "`${1}$Version")
    Set-Content -LiteralPath $propsPath -Value $updated -NoNewline -Encoding UTF8
    Write-Ok "gradle.properties $current -> $Version."
}

# Every Minecraft release from $MinGameVersion onward, in Modrinth's own order.
# Snapshots and pre-releases are excluded.
function Get-GameVersions {
    $tags = Invoke-RestMethod -Uri 'https://api.modrinth.com/v2/tag/game_version' `
                              -Headers @{ 'User-Agent' = $UserAgent }

    $releases = @($tags | Where-Object { $_.version_type -eq 'release' } | ForEach-Object { $_.version })
    # The tag list is newest-first; flip it so the published array reads oldest-first.
    [array]::Reverse($releases)

    $startIndex = [array]::IndexOf($releases, $MinGameVersion)
    if ($startIndex -lt 0) { throw "Minimum game version '$MinGameVersion' not found in Modrinth's tag list." }

    return @($releases[$startIndex..($releases.Count - 1)])
}

function Invoke-Build {
    Write-Step 'Building'
    $gradlew = Join-Path $RootDir 'gradlew.bat'
    if (-not (Test-Path $gradlew)) { throw "gradlew.bat not found at $gradlew" }

    & $gradlew --project-dir $RootDir clean build
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed with exit code $LASTEXITCODE." }
    Write-Ok 'Build succeeded.'
}

# Guards against shipping a jar whose bytecode target drifted off Java 11 — it
# would compile and package cleanly but fail to load on the target servers.
function Assert-JarTarget {
    param([Parameter(Mandatory)] [string] $JarPath)

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($JarPath)
    try {
        $entry = $zip.Entries | Where-Object { $_.FullName -eq 'host/plas/flyingallowed/FlyingAllowed.class' }
        if (-not $entry) { throw 'Main class not found in the jar.' }

        $stream = $entry.Open()
        try {
            $header = [byte[]]::new(8)
            $read = $stream.Read($header, 0, 8)
            if ($read -lt 8) { throw 'Could not read the class file header.' }
        } finally { $stream.Dispose() }

        # Bytes 6-7 of a class file are the big-endian major version; 55 is Java 11.
        $major = ($header[6] -shl 8) -bor $header[7]
        if ($major -ne 55) {
            throw "Jar targets class file major version $major, expected 55 (Java 11). It will not load on target servers."
        }
        Write-Ok "Bytecode target verified (major $major = Java 11)."
    } finally { $zip.Dispose() }
}

# ---------------------------------------------------------------------------

$release = Get-Release
$version = $release.Version
$changelog = $release.Changelog

Write-Step "FlyingAllowed $version"
Write-Ok "Changelog read from UPDATES.md ($(($changelog -split "`n").Count) lines)."

# Writing the version before the build is what makes the jar's name and its
# embedded plugin.yml match the version being published.
Sync-ProjectVersion -Version $version

$token = $env:MODRINTH_TOKEN
if ([string]::IsNullOrWhiteSpace($token)) {
    if ($Confirm) {
        throw 'MODRINTH_TOKEN is not set. Set it to a token with the VERSION_CREATE scope.'
    }
    Write-Warn 'MODRINTH_TOKEN is not set (not required for a dry run).'
}

if (-not $SkipBuild) { Invoke-Build } else { Write-Warn 'Skipping build (-SkipBuild).' }

$jarPath = Join-Path $RootDir "target\libs\FlyingAllowed-$version.jar"
if (-not (Test-Path $jarPath)) {
    throw "Expected jar not found at $jarPath. Run without -SkipBuild to build it."
}
Assert-JarTarget -JarPath $jarPath

$jarInfo = Get-Item $jarPath
Write-Ok ("Jar: {0} ({1:N0} KB)" -f $jarInfo.Name, ($jarInfo.Length / 1KB))

Write-Step 'Resolving supported game versions'
$gameVersions = Get-GameVersions
Write-Ok "$($gameVersions.Count) versions: $($gameVersions[0]) .. $($gameVersions[-1])"

# Reject a version number that is already published, rather than letting
# Modrinth return an opaque error part-way through the upload.
Write-Step 'Checking for an existing version'
try {
    $existing = Invoke-RestMethod -Uri "https://api.modrinth.com/v2/project/$ProjectId/version" `
                                  -Headers @{ 'User-Agent' = $UserAgent }
    if ($existing | Where-Object { $_.version_number -eq $version }) {
        throw "Version $version is already published on Modrinth. Bump the version in gradle.properties."
    }
    Write-Ok "Version $version is not yet published."
} catch [System.Net.WebException] {
    Write-Warn "Could not reach Modrinth to check existing versions: $($_.Exception.Message)"
}

$metadata = [ordered]@{
    name           = $version
    version_number = $version
    changelog      = $changelog
    dependencies   = @(
        [ordered]@{ project_id = $BouProjectId; dependency_type = 'required' }
    )
    game_versions  = $gameVersions
    version_type   = $VersionType
    loaders        = $Loaders
    featured       = $false
    project_id     = $ProjectId
    file_parts     = @('file')
    primary_file   = 'file'
}

if (-not $Confirm) {
    Write-Step 'DRY RUN — nothing was uploaded'
    Write-Host ''
    Write-Host "  Project      : $ProjectId"
    Write-Host "  Version      : $version ($VersionType)"
    Write-Host "  File         : $($jarInfo.FullName)"
    Write-Host "  Loaders      : $($Loaders -join ', ')"
    Write-Host "  Game versions: $($gameVersions.Count) ($($gameVersions[0]) .. $($gameVersions[-1]))"
    Write-Host "  Dependency   : $BouProjectId (required)"
    Write-Host ''
    Write-Host '  Changelog:' -ForegroundColor Cyan
    $changelog -split "`n" | ForEach-Object { Write-Host "    $_" }
    Write-Host ''
    Write-Host '  Re-run with -Confirm to publish.' -ForegroundColor Yellow
    return
}

Write-Step "Publishing $version to Modrinth"

$form = @{
    data = ($metadata | ConvertTo-Json -Depth 6 -Compress)
    file = Get-Item -LiteralPath $jarPath
}

try {
    $response = Invoke-RestMethod -Uri 'https://api.modrinth.com/v2/version' `
                                  -Method Post `
                                  -Headers @{ Authorization = $token; 'User-Agent' = $UserAgent } `
                                  -Form $form
} catch {
    $detail = $_.ErrorDetails.Message
    if ($detail) { Write-Host $detail -ForegroundColor Red }
    throw "Publish failed: $($_.Exception.Message)"
}

Write-Ok "Published $version."
Write-Ok "https://modrinth.com/plugin/$ProjectId/version/$($response.id)"
