<#
.SYNOPSIS
    Clears build output so a subsequent build starts from a clean tree.

.DESCRIPTION
    `gradlew clean` fails when anything holds a handle on target\, which is the
    normal state for a project inside OneDrive: the sync client and the IDE
    indexer both keep build output open. This script stops the processes that
    hold those handles, then deletes the directories directly.

    Build output is regenerable, so it is deleted permanently rather than sent
    to the Recycle Bin.

.PARAMETER KeepDaemons
    Leave Gradle daemons running. They are stopped by default because a daemon
    holds file handles under target\ and, after a Gradle upgrade, stale
    incompatible daemons linger.

.PARAMETER StopOneDrive
    Stop the OneDrive sync client. It reopens files under its sync root while
    uploading them, which is what makes build output inside OneDrive
    intermittently undeletable. Off by default because it is a system-wide
    action; OneDrive restarts at next logon or from the Start menu.

.PARAMETER IncludeCaches
    Also remove .gradle\ (the project-local Gradle cache). Slower next build;
    use when the cache itself is suspect.

.EXAMPLE
    .\clean-builds.ps1
    Stop daemons and delete target\ and build\.

.EXAMPLE
    .\clean-builds.ps1 -StopOneDrive
    Also stop OneDrive first, for when deletion keeps failing.
#>
[CmdletBinding()]
param(
    [switch] $KeepDaemons,
    [switch] $StopOneDrive,
    [switch] $IncludeCaches
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$RootDir = $PSScriptRoot

function Write-Step { param([string] $Message) Write-Host "==> $Message" -ForegroundColor Cyan }
function Write-Ok   { param([string] $Message) Write-Host "    $Message" -ForegroundColor Green }
function Write-Warn { param([string] $Message) Write-Host "    $Message" -ForegroundColor Yellow }

# A Gradle daemon keeps handles on the output it produced, so it must go before
# the directory can be removed. Stopping is also the only way to retire daemons
# left incompatible by a Gradle version change.
function Stop-GradleDaemons {
    Write-Step 'Stopping Gradle daemons'

    $gradlew = Join-Path $RootDir 'gradlew.bat'
    if (Test-Path $gradlew) {
        & $gradlew --project-dir $RootDir --stop 2>&1 | ForEach-Object { Write-Verbose $_ }
    }

    # --stop only retires daemons matching the current Gradle version; one left
    # incompatible by a version change survives it and keeps its locks.
    $stragglers = @(Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -and $_.CommandLine -match 'GradleDaemon' })

    foreach ($proc in $stragglers) {
        try {
            Stop-Process -Id $proc.ProcessId -Force -ErrorAction Stop
            Write-Ok "Killed leftover daemon (pid $($proc.ProcessId))."
        } catch {
            Write-Warn "Could not stop pid $($proc.ProcessId): $($_.Exception.Message)"
        }
    }

    if ($stragglers.Count -eq 0) { Write-Ok 'No leftover daemons.' }
}

function Stop-OneDriveClient {
    $oneDrive = @(Get-Process -Name OneDrive -ErrorAction SilentlyContinue)
    if ($oneDrive.Count -eq 0) {
        Write-Ok 'OneDrive is not running.'
        return
    }

    Write-Step 'Stopping OneDrive'
    foreach ($proc in $oneDrive) {
        try {
            Stop-Process -Id $proc.Id -Force -ErrorAction Stop
            Write-Ok "Stopped OneDrive (pid $($proc.Id)); it restarts at next logon."
        } catch {
            Write-Warn "Could not stop OneDrive: $($_.Exception.Message)"
        }
    }
}

# Retries because a handle released by a stopped process is not always gone by
# the time the next delete is attempted.
function Remove-BuildDirectory {
    param([Parameter(Mandatory)] [string] $Path)

    $name = Split-Path $Path -Leaf

    for ($attempt = 1; $attempt -le 3; $attempt++) {
        try {
            Remove-Item -LiteralPath $Path -Recurse -Force -ErrorAction Stop
            Write-Ok "Removed $name\"
            return $true
        } catch {
            if ($attempt -eq 3) {
                Write-Warn "Could not remove $name\: $($_.Exception.Message)"
                if (-not $StopOneDrive -and $RootDir -like '*OneDrive*') {
                    Write-Warn 'This project is under OneDrive; retry with -StopOneDrive.'
                }
                Write-Warn 'If it persists, close the IDE (its indexer holds build output open).'
                return $false
            }
            Start-Sleep -Milliseconds (400 * $attempt)
        }
    }
    return $false
}

# ---------------------------------------------------------------------------

Write-Step "Cleaning $(Split-Path $RootDir -Leaf)"

if ($KeepDaemons) { Write-Warn 'Leaving daemons running (-KeepDaemons).' } else { Stop-GradleDaemons }

if ($StopOneDrive) { Stop-OneDriveClient }

Write-Step 'Removing build output'

$targets = [System.Collections.Generic.List[string]]::new()
$targets.Add((Join-Path $RootDir 'target'))
$targets.Add((Join-Path $RootDir 'build'))
if ($IncludeCaches) { $targets.Add((Join-Path $RootDir '.gradle')) }

$present = 0
$removed = 0
foreach ($path in $targets) {
    if (Test-Path $path) {
        $present++
        if (Remove-BuildDirectory -Path $path) { $removed++ }
    }
}

if ($present -eq 0) {
    Write-Ok 'Nothing to remove; tree is already clean.'
} elseif ($removed -lt $present) {
    throw 'Some build output could not be removed.'
}

Write-Step 'Clean'
