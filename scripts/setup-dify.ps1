# Deploy Dify for Phase 3
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Requires: Docker Desktop with WSL2 on Windows Server 2025

$ErrorActionPreference = "Stop"
$DifyDir = Join-Path $PSScriptRoot "..\dify" | Resolve-Path

Write-Host "=== Dify Deployment (Phase 3) ===" -ForegroundColor Cyan
Write-Host "Author: SIVARAMAN R"

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Docker not found. Install Docker Desktop + WSL2 first." -ForegroundColor Red
    Write-Host "See docs/DIFY_SETUP.md"
    exit 1
}

Set-Location $DifyDir

if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "Created dify/.env from example — edit secrets before production."
}

Write-Host "Starting Dify stack..."
docker compose up -d

Write-Host ""
Write-Host "Dify Web UI:  http://localhost:3000" -ForegroundColor Green
Write-Host "Dify API:     http://localhost:5001" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:"
Write-Host "  1. Open http://localhost:3000 and create admin account"
Write-Host "  2. Add xAI Grok as model provider in Settings"
Write-Host "  3. Create an app and copy API key to application-local.yml:"
Write-Host "       agent.dify.enabled: true"
Write-Host "       agent.dify.api-key: app-xxxxxxxx"
