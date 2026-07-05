# Start Persistent Agent Platform
# Author: SIVARAMAN R <sivaram311@gmail.com>

$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

Write-Host "Starting Persistent Agent Platform..."
Write-Host "Profile: local | URL: http://localhost:8080"
Write-Host "Author: SIVARAMAN R"

mvn spring-boot:run "-Dspring-boot.run.profiles=local"
