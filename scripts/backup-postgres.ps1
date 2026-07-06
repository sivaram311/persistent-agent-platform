# PostgreSQL backup script
# Author: SIVARAMAN R <sivaram311@gmail.com>

$BackupDir = "E:\Backups\agent-platform"
$PgDump = "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe"
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$BackupFile = Join-Path $BackupDir "agent_platform_$Timestamp.dump"

if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

$env:PGPASSWORD = $env:DB_PASSWORD
if (-not $env:PGPASSWORD) { $env:PGPASSWORD = "AgentPlatform2026!" }

& $PgDump -U agent_user -h localhost -d agent_platform -F c -f $BackupFile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Backup created: $BackupFile"
    Get-ChildItem $BackupDir -Filter "*.dump" | Sort-Object LastWriteTime -Descending |
        Select-Object -Skip 14 | Remove-Item -Force
} else {
    Write-Host "Backup failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit 1
}

Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
