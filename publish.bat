@echo off
REM Publishes FlyingAllowed to Modrinth. Thin wrapper around publish.ps1, which
REM holds the actual logic; arguments are passed straight through.
REM
REM   publish.bat              dry run - builds and prints what would be uploaded
REM   publish.bat -Confirm     builds and publishes
REM
REM Requires MODRINTH_TOKEN in the environment for an actual publish.

setlocal

where pwsh >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set "PS_EXE=pwsh"
) else (
    REM Windows PowerShell 5.1 lacks Invoke-RestMethod -Form, which the upload needs.
    echo [publish] pwsh ^(PowerShell 7+^) not found on PATH.
    echo [publish] Install it from https://aka.ms/powershell - Windows PowerShell 5.1 cannot upload.
    exit /b 1
)

%PS_EXE% -NoProfile -ExecutionPolicy Bypass -File "%~dp0publish.ps1" %*
exit /b %ERRORLEVEL%
