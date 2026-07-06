# Install Caddy reverse proxy
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

$CaddyDir = "C:\caddy"
$Caddyfile = Join-Path $PSScriptRoot "Caddyfile"

if (-not (Test-Path $CaddyDir)) {
    New-Item -ItemType Directory -Path $CaddyDir -Force | Out-Null
}

Copy-Item $Caddyfile (Join-Path $CaddyDir "Caddyfile") -Force
Write-Host "Caddyfile copied to C:\caddy\Caddyfile"
Write-Host ""
Write-Host "Download Caddy for Windows from: https://caddyserver.com/download"
Write-Host "Then install as NSSM service:"
Write-Host "  nssm install Caddy C:\caddy\caddy.exe run --config C:\caddy\Caddyfile"
Write-Host "  net start Caddy"
