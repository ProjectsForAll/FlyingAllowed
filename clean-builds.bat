@echo off
REM Clears build output. Thin wrapper around clean-builds.ps1, which holds the
REM actual logic; arguments are passed straight through.
REM
REM   clean-builds.bat                  stop daemons, delete target\ and build\
REM   clean-builds.bat -StopOneDrive    also stop OneDrive, if deletes keep failing
REM   clean-builds.bat -IncludeCaches   also drop the project-local .gradle\ cache

setlocal

REM PATH alone is not a reliable way to find PowerShell 7. A Microsoft Store
REM install lives under a versioned WindowsApps directory that Windows injects
REM into the environment at logon, so it is absent from the registry PATH and
REM disappears from any session whose PATH was rebuilt from the registry. Fall
REM back to the known install locations, including the App Execution Alias.
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

REM Unlike the upload in publish.ps1, nothing here needs PowerShell 7, so fall
REM back to Windows PowerShell rather than failing.
if not defined PS_EXE set "PS_EXE=powershell"

"%PS_EXE%" -NoProfile -ExecutionPolicy Bypass -File "%~dp0clean-builds.ps1" %*
exit /b %ERRORLEVEL%
