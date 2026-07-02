@echo off
setlocal
set GRADLE_VERSION=8.10.2
set GRADLE_DIR=%USERPROFILE%\.gradle\codex-dists\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%GRADLE_DIR%\bin\gradle.bat
if not exist "%GRADLE_BIN%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $v='%GRADLE_VERSION%'; $base=Join-Path $env:USERPROFILE '.gradle\codex-dists'; $zip=Join-Path $base ('gradle-' + $v + '-bin.zip'); New-Item -ItemType Directory -Force -Path $base | Out-Null; Invoke-WebRequest -Uri ('https://services.gradle.org/distributions/gradle-' + $v + '-bin.zip') -OutFile $zip; Expand-Archive -Force $zip $base"
)
call "%GRADLE_BIN%" %*
