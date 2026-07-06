# Production Deployment (Phase 4 — Complete)

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com

---

## Phase 4 Checklist

| Item | Script / Config | Status |
|------|-----------------|--------|
| Spring Boot NSSM service | `scripts/install-nssm-service.ps1` | Ready |
| LangGraph NSSM service | `scripts/install-langgraph-service.ps1` | Ready |
| Full production install | `scripts/install-production.ps1` | Ready |
| Firewall rules | `scripts/configure-firewall.ps1` | Ready |
| PostgreSQL daily backup | `scripts/backup-postgres.ps1` + `setup-backup-task.ps1` | Ready |
| HTTPS reverse proxy | `scripts/Caddyfile` + `install-caddy.ps1` | Ready |
| Authentication | `agent.security.enabled=true` | Ready |
| Workspace sandbox | `WorkspaceGuardService` | Implemented |
| CLI approval gates | `CliApprovalService` | Implemented |
| File logging + rotation | `logback-spring.xml` → `E:/Logs/agent-platform` | Implemented |

---

## One-command production install

```powershell
# Run as Administrator
cd E:\MyWorkspace\persistent-agent-platform
.\scripts\install-production.ps1
```

This installs:
1. Spring Boot as Windows service (`PersistentAgentPlatform`)
2. LangGraph sidecar as Windows service (`LangGraphSidecar`)
3. Firewall rules (8080, 443, 8090, 3000)
4. Daily PostgreSQL backup scheduled task (2 AM)

---

## Enable authentication

Copy `application-production.yml.example` settings into `application-local.yml`:

```yaml
agent:
  security:
    enabled: true
    username: admin
    password: YOUR_STRONG_PASSWORD
```

Or environment variables:
```
SECURITY_ENABLED=true
AGENT_USERNAME=admin
AGENT_PASSWORD=your_strong_password
```

When enabled:
- `/api/v1/chat`, `/api/v1/sessions` require HTTP Basic Auth
- `/api/v1/health`, `/index.html`, `/ws/chat` remain public

---

## HTTPS with Caddy

1. Edit `scripts/Caddyfile` — replace `yourdomain.com`
2. Download Caddy: https://caddyserver.com/download
3. Run:
```powershell
.\scripts\install-caddy.ps1
nssm install Caddy C:\caddy\caddy.exe run --config C:\caddy\Caddyfile
net start Caddy
```

---

## Security features (built-in)

### Workspace isolation
Agent file access limited to `agent.workspace-root` (`E:/MyWorkspace`). Paths outside are rejected.

### CLI approval gates
Prompts containing destructive patterns are blocked:
- `rm -rf`, `del /f`, `format c:`, `drop database`, `shutdown`, etc.

Returns HTTP 403 with error message.

---

## Logging

Logs written to: `E:\Logs\agent-platform\platform.log`  
Daily rotation, 30-day retention.

Set custom path: `LOG_PATH=D:/Logs/agent`

---

## Backup

Manual:
```powershell
.\scripts\backup-postgres.ps1
```

Automatic: daily at 2 AM via scheduled task `AgentPlatform-PostgresBackup`  
Backups stored in: `E:\Backups\agent-platform\` (14-day retention)

---

## Verify production

```powershell
.\scripts\verify-api.ps1
curl.exe -s http://localhost:8080/api/v1/health
curl.exe -s http://localhost:8090/api/v1/health
sc query PersistentAgentPlatform
sc query LangGraphSidecar
```

---

*Maintained by SIVARAMAN R*
