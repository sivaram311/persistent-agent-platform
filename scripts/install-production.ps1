# Install all Phase 4 production services
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

Write-Host "=== Phase 4 Production Install ===" -ForegroundColor Cyan
Write-Host "Author: SIVARAMAN R"
Write-Host ""

Write-Host "[1/4] Spring Boot platform..."
& "$PSScriptRoot\install-nssm-service.ps1"

Write-Host ""
Write-Host "[2/4] LangGraph sidecar..."
& "$PSScriptRoot\install-langgraph-service.ps1"

Write-Host ""
Write-Host "[3/4] Firewall rules..."
& "$PSScriptRoot\configure-firewall.ps1"

Write-Host ""
Write-Host "[4/4] PostgreSQL backup task..."
& "$PSScriptRoot\setup-backup-task.ps1"

Write-Host ""
Write-Host "=== Phase 4 install complete ===" -ForegroundColor Green
Write-Host "Enable HTTPS: copy scripts/Caddyfile to C:\caddy\ and run Caddy"
Write-Host "Enable auth: set SECURITY_ENABLED=true in application-local.yml"
