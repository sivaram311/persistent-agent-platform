# Setup Guide

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Project:** Persistent Agent Platform  
**Last updated:** 2026-07-06

---

## 1. Prerequisites

| Requirement | Version | Verified on VPS |
|-------------|---------|-----------------|
| Java (Temurin) | 21+ | OpenJDK 21.0.11 |
| Maven | 3.9+ | 3.9.16 |
| PostgreSQL | 18+ | 18.4 at `C:\Program Files\PostgreSQL\18` |
| Cursor Agent CLI | latest | `C:\Users\Administrator\AppData\Local\cursor-agent\cursor-agent.cmd` |
| Antigravity CLI | latest | `C:\Users\Administrator\AppData\Local\agy\bin\agy.exe` |
| Grok Build CLI | optional | Not in PATH (optional for Solution 1) |

---

## 2. Directory Layout

```
E:\MyWorkspace\
└── persistent-agent-platform\          ← Spring Boot project root
    ├── docs\                           ← Documentation
    ├── scripts\                        ← Setup and verification scripts
    ├── langgraph-sidecar\              ← Phase 2 Python sidecar (planned)
    ├── src\main\
    │   ├── java\com\sivaraman\agentplatform\
    │   └── resources\
    │       ├── application.yml         ← Default config (committed)
    │       ├── application-local.yml   ← Local secrets (NOT committed)
    │       └── db\migration\           ← Flyway SQL migrations
    └── pom.xml
```

---

## 3. PostgreSQL Setup

PostgreSQL 18 is installed at `C:\Program Files\PostgreSQL\18`. Service name: `postgresql-x64-18`.

### Option A — Automated (PowerShell)

```powershell
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
$env:PGPASSWORD = "postgres"   # postgres superuser password

& $psql -U postgres -h localhost -c "CREATE DATABASE agent_platform;"
& $psql -U postgres -h localhost -c "CREATE USER agent_user WITH ENCRYPTED PASSWORD 'AgentPlatform2026!';"
& $psql -U postgres -h localhost -c "ALTER DATABASE agent_platform OWNER TO agent_user;"
& $psql -U postgres -h localhost -d agent_platform -c "GRANT ALL ON SCHEMA public TO agent_user;"

Remove-Item Env:PGPASSWORD
```

Or run the SQL script:

```powershell
$env:PGPASSWORD = "postgres"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -f scripts\setup-database.sql
Remove-Item Env:PGPASSWORD
```

### Option B — Manual SQL

See `scripts/setup-database.sql`.

### Verify database

```powershell
$env:PGPASSWORD = "AgentPlatform2026!"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U agent_user -h localhost -d agent_platform -c "\dt"
Remove-Item Env:PGPASSWORD
```

Expected tables: `agent_sessions`, `conversation_messages`, `consciousness_snapshots`, `memory_entries`, `cli_execution_logs`, `solution_routing_rules`, `flyway_schema_history`.

---

## 4. Local Configuration

Copy the example file and edit credentials:

```powershell
Copy-Item src\main\resources\application-local.yml.example `
          src\main\resources\application-local.yml
```

Edit `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/agent_platform
    username: agent_user
    password: AgentPlatform2026!

agent:
  workspace-root: E:/MyWorkspace/persistent-agent-platform
  cli:
    cursor-agent-path: C:/Users/Administrator/AppData/Local/cursor-agent/cursor-agent.cmd
    antigravity-path: C:/Users/Administrator/AppData/Local/agy/bin/agy.exe
```

> **Note:** `application-local.yml` is gitignored and must never be committed.

---

## 5. Build and Run

### Build

```powershell
cd E:\MyWorkspace\persistent-agent-platform
mvn clean compile
```

### Run (local profile)

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Or use the start script:

```powershell
.\scripts\start.ps1
```

### Access

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Chat UI (redirects to `/index.html`) |
| http://localhost:8080/index.html | Chat UI direct |
| http://localhost:8080/api/v1/health | Health check API |
| http://localhost:8080/actuator/health | Spring Actuator health |

Flyway runs automatically on startup and applies `V1__initial_schema.sql`.

---

## 6. Verify with curl

Run the verification script:

```powershell
.\scripts\verify-api.ps1
```

Or manually:

```powershell
curl.exe -s http://localhost:8080/api/v1/health
curl.exe -s -X POST http://localhost:8080/api/v1/chat `
  -H "Content-Type: application/json" `
  --data-binary "@scripts\curl-chat-test.json"
```

See [API Reference](API.md) for full curl examples.

---

## 7. Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | agent_platform | Database name |
| `DB_USER` | agent_user | Database user |
| `DB_PASSWORD` | changeme | Database password |
| `SERVER_PORT` | 8080 | HTTP port |
| `AGENT_WORKSPACE` | E:/MyWorkspace | Agent file sandbox |
| `CURSOR_AGENT_PATH` | cursor-agent | Full path recommended on Windows |
| `ANTIGRAVITY_PATH` | agy | Full path recommended on Windows |
| `GROK_PATH` | grok | Grok Build CLI path |
| `LANGGRAPH_ENABLED` | false | Enable Solution 2 |
| `LANGGRAPH_URL` | http://localhost:8090 | LangGraph sidecar URL |
| `DIFY_ENABLED` | false | Enable Solution 3 |
| `DIFY_URL` | http://localhost:3000 | Dify API base URL |
| `DIFY_API_KEY` | (empty) | Dify API key |

---

## 8. Troubleshooting

| Problem | Fix |
|---------|-----|
| `CreateProcess error=2` for cursor-agent | Set full path in `application-local.yml` |
| Workspace Trust Required | `--trust` flag is already set in `CliAgentService` |
| Flyway migration fails | Ensure `agent_user` owns `agent_platform` database |
| Maven profile not applied | Use quotes: `"-Dspring-boot.run.profiles=local"` |
| curl chat returns 400 | Use `--data-binary "@file.json"` to avoid PowerShell escaping issues |
| Chat takes 30–60 seconds | Normal — Cursor Agent CLI runs synchronously |

---

*Maintained by SIVARAMAN R (sivaram311@gmail.com)*
