# Schedule daily PostgreSQL backup at 2 AM
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

$TaskName = "AgentPlatform-PostgresBackup"
$ScriptPath = Join-Path $PSScriptRoot "backup-postgres.ps1"

$action = New-ScheduledTaskAction -Execute "powershell.exe" -Argument "-ExecutionPolicy Bypass -File `"$ScriptPath`""
$trigger = New-ScheduledTaskTrigger -Daily -At "02:00"
$settings = New-ScheduledTaskSettingsSet -StartWhenAvailable -DontStopOnIdleEnd

Register-ScheduledTask -TaskName $TaskName -Action $action -Trigger $trigger `
    -Settings $settings -Description "Daily PostgreSQL backup for Persistent Agent Platform" `
    -Force

Write-Host "Scheduled task registered: $TaskName (daily 2:00 AM)"
