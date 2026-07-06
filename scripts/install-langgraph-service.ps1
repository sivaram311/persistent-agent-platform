# Install LangGraph sidecar as Windows service (NSSM)
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

$ErrorActionPreference = "Stop"
$NssmDir = "C:\nssm"
$SidecarRoot = Join-Path $PSScriptRoot "..\langgraph-sidecar" | Resolve-Path
$VenvPython = Join-Path $SidecarRoot ".venv\Scripts\python.exe"
$ServiceName = "LangGraphSidecar"

if (-not (Test-Path $VenvPython)) {
    Write-Host "Creating Python venv..."
    Set-Location $SidecarRoot
    python -m venv .venv
    & $VenvPython -m pip install -r requirements.txt
}

if (-not (Test-Path (Join-Path $SidecarRoot ".env"))) {
    Copy-Item (Join-Path $SidecarRoot ".env.example") (Join-Path $SidecarRoot ".env")
    Write-Host "Created langgraph-sidecar/.env — set XAI_API_KEY for Grok mode."
}

$NssmExe = Get-ChildItem -Path $NssmDir -Recurse -Filter "nssm.exe" -ErrorAction SilentlyContinue |
    Select-Object -First 1 -ExpandProperty FullName
if (-not $NssmExe) {
    Write-Host "Downloading NSSM..."
    $zip = "$env:TEMP\nssm.zip"
    Invoke-WebRequest -Uri "https://nssm.cc/release/nssm-2.24.zip" -OutFile $zip
    Expand-Archive $zip -DestinationPath $NssmDir -Force
    $NssmExe = Get-ChildItem -Path $NssmDir -Recurse -Filter "nssm.exe" | Select-Object -First 1 -ExpandProperty FullName
}

Write-Host "Installing service: $ServiceName"
& $NssmExe stop $ServiceName 2>$null
& $NssmExe remove $ServiceName confirm 2>$null
& $NssmExe install $ServiceName $VenvPython "main.py"
& $NssmExe set $ServiceName AppDirectory $SidecarRoot
& $NssmExe set $ServiceName DisplayName "LangGraph Agent Sidecar"
& $NssmExe set $ServiceName Description "Solution 2 — LangGraph sidecar by SIVARAMAN R"
& $NssmExe set $ServiceName Start SERVICE_AUTO_START

net start $ServiceName
Write-Host "LangGraph sidecar running on http://localhost:8090" -ForegroundColor Green
