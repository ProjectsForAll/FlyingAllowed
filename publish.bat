@echo off
REM Publishes FlyingAllowed to Modrinth. Thin wrapper around publish.ps1, which
REM holds the actual logic; arguments are passed straight through.
REM
REM   publish.bat              dry run - builds and prints what would be uploaded
REM   publish.bat -Confirm     builds and publishes
REM
REM Requires MODRINTH_TOKEN in the environment for an actual publish.

setlocal

REM PowerShell 7+ is required: Windows PowerShell 5.1 lacks Invoke-RestMethod
REM -Form, which the multipart upload needs.
REM
REM PATH alone is not a reliable way to find it. A Microsoft Store install lives
REM under a versioned WindowsApps directory that Windows injects into the
REM environment at logon, so it is absent from the registry PATH and disappears
REM from any session whose PATH was rebuilt from the registry. Fall back to the
REM known install locations, including the App Execution Alias.
set "PS_EXE="

where pwsh >nul 2>&1 && set "PS_EXE=pwsh"

if not defined PS_EXE (
    for %%P in (
        "%LOCALAPPDATA%\Microsoft\WindowsApps\pwsh.exe"
        "%ProgramFiles%\PowerShell\7\pwsh.exe"
        "%ProgramFiles(x86)%\PowerShell\7\pwsh.exe"
    ) do (
        if not defined PS_EXE if exist "%%~P" set "PS_EXE=%%~P"
    )
)

if not defined PS_EXE (
    for /d %%D in ("%ProgramFiles%\WindowsApps\Microsoft.PowerShell_*_x64__8wekyb3d8bbwe") do (
        if not defined PS_EXE if exist "%%~D\pwsh.exe" set "PS_EXE=%%~D\pwsh.exe"
    )
)

if not defined PS_EXE (
    echo [publish] PowerShell 7+ ^(pwsh^) not found.
    echo [publish] Install it from https://aka.ms/powershell - Windows PowerShell 5.1 cannot upload.
    exit /b 1
)

"%PS_EXE%" -NoProfile -ExecutionPolicy Bypass -File "%~dp0publish.ps1" %*
exit /b %ERRORLEVEL%
