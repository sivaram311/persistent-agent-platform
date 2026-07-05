# Production deployment guide
# Author: SIVARAMAN R <sivaram311@gmail.com>

## Phase 4 Checklist

### 1. Enable authentication

In `application-local.yml`:

```yaml
agent:
  security:
    enabled: true
    username: your_username
    password: your_strong_password
```

Or environment:
```
SECURITY_ENABLED=true
AGENT_USERNAME=admin
AGENT_PASSWORD=your_strong_password
```

### 2. Build and install Windows service

```powershell
# Run as Administrator
.\scripts\install-nssm-service.ps1
```

### 3. HTTPS with Caddy

1. Download Caddy for Windows: https://caddyserver.com/download
2. Copy `scripts/Caddyfile` to `C:\caddy\Caddyfile`
3. Replace `yourdomain.com` with your domain
4. Install Caddy as NSSM service pointing to `caddy run`

### 4. Firewall

```powershell
New-NetFirewallRule -DisplayName "Agent-HTTPS" -Direction Inbound -LocalPort 443 -Protocol TCP -Action Allow
```

### 5. PostgreSQL backup

```powershell
# Daily backup task
& "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe" -U agent_user -d agent_platform -F c -f "E:\Backups\agent_platform_$(Get-Date -Format yyyyMMdd).dump"
```

### 6. Verify

```powershell
.\scripts\verify-api.ps1
curl.exe -s https://yourdomain.com/api/v1/health
```

---

See also: `docs/SETUP.md`, `PROJECT_CONSCIOUSNESS.md`
