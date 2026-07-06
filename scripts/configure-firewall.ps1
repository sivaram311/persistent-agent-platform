# Configure Windows Firewall for production
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

Write-Host "Configuring firewall rules..."

$rules = @(
    @{ Name = "Agent-HTTP"; Port = 8080; Desc = "Persistent Agent Platform HTTP" },
    @{ Name = "Agent-HTTPS"; Port = 443; Desc = "Caddy HTTPS reverse proxy" },
    @{ Name = "LangGraph-Sidecar"; Port = 8090; Desc = "LangGraph sidecar internal" },
    @{ Name = "Dify-Web"; Port = 3000; Desc = "Dify web UI" }
)

foreach ($r in $rules) {
    $existing = Get-NetFirewallRule -DisplayName $r.Name -ErrorAction SilentlyContinue
    if ($existing) {
        Write-Host "  Rule exists: $($r.Name)"
        continue
    }
    New-NetFirewallRule -DisplayName $r.Name -Direction Inbound -LocalPort $r.Port `
        -Protocol TCP -Action Allow -Description $r.Desc | Out-Null
    Write-Host "  Created: $($r.Name) (port $($r.Port))"
}

Write-Host "Firewall configuration complete."
