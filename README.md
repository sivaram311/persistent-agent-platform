# Persistent Agent Platform

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Repository:** https://github.com/sivaram311/persistent-agent-platform  
**Location:** `E:\MyWorkspace\persistent-agent-platform`  
**Version:** 0.1.0-SNAPSHOT

---

## Executive Summary

The **Persistent Agent Platform** is a self-hosted, always-on AI agent system for **Windows Server 2025 VPS**. It unifies three agent architectures into one Spring Boot application backed by **PostgreSQL 18**, with persistent conversation history and a consciousness layer that maintains context across sessions.

| Goal | Approach |
|------|----------|
| Browser-accessible 24/7 agent | Spring Boot REST API + web chat UI on port 8080 |
| Use installed CLI agents | Solution 1 wraps Cursor Agent, Antigravity, Grok Build |
| Advanced orchestration | Solution 2 integrates LangGraph as a Python sidecar |
| Visual agent builder | Solution 3 integrates Dify.ai via API |
| Persistent memory | PostgreSQL stores messages, sessions, consciousness snapshots |
| Tech stack | Java 21, Spring Boot 3.4, Flyway, PostgreSQL 18 |

---

## Current Status (2026-07-06)

**Phases 1–4 Complete | Phases 5–6 Pending**

| Phase | Status |
|-------|--------|
| 1 Foundation | ✅ Complete |
| 2 LangGraph | ✅ Complete |
| 3 Dify | ✅ Complete (run `setup-dify.ps1`) |
| 4 Production | ✅ Complete (run `install-production.ps1`) |
| 5 Enhancements | ⏸ Pending |
| 6 Advanced scale | ⏸ Pending |

---

## Quick Start

```powershell
# 1. Setup database (once)
$env:PGPASSWORD = "postgres"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -h localhost -f scripts\setup-database.sql

# 2. Copy local config
Copy-Item src\main\resources\application-local.yml.example `
          src\main\resources\application-local.yml
# Edit passwords and CLI paths in application-local.yml

# 3. Start application
.\scripts\start.ps1

# 4. Verify
.\scripts\verify-api.ps1
```

Open **http://localhost:8080** in your browser.

---

## Architecture

```
Browser → Spring Boot (:8080) → PostgreSQL 18
              ├── Solution 1: cursor-agent / agy / grok
              ├── Solution 2: LangGraph sidecar (:8090)
              └── Solution 3: Dify.ai (:3000)
                    ↓
              E:\MyWorkspace (file sandbox)
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Health check |
| POST | `/api/v1/chat` | Send message to agent |
| GET | `/api/v1/sessions/{id}/history` | Conversation history |
| GET | `/index.html` | Web chat UI |

**Example:**

```bash
curl.exe -s http://localhost:8080/api/v1/health
curl.exe -s -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  --data-binary "@scripts/curl-chat-test.json"
```

---

## Documentation

| Document | Description |
|----------|-------------|
| **[PROJECT_CONSCIOUSNESS.md](PROJECT_CONSCIOUSNESS.md)** | **AI context file — read first** |
| [Setup Guide](docs/SETUP.md) | PostgreSQL, config, build, run, troubleshooting |
| [API Reference](docs/API.md) | All endpoints with curl examples and sample responses |
| [Architecture](docs/ARCHITECTURE.md) | System design, package structure, request flow |
| [Project Status](docs/PROJECT_STATUS.md) | What's done, curl verification results, next steps |
| [Action Plan](docs/ACTION_PLAN.md) | Full 6-phase implementation roadmap |
| [Phase 5 & 6 Pending](docs/PHASE_PENDING.md) | Deferred enhancements and scale items |
| [Production](docs/PRODUCTION.md) | Auth, NSSM, Caddy HTTPS deployment |
| [Dify Setup](docs/DIFY_SETUP.md) | Phase 3 Dify Docker deployment |

---

## Configuration

Environment variables (or `application-local.yml`):

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | changeme | PostgreSQL password |
| `AGENT_WORKSPACE` | E:/MyWorkspace | Agent file sandbox |
| `LANGGRAPH_ENABLED` | false | Enable Solution 2 |
| `DIFY_ENABLED` | false | Enable Solution 3 |

See [Setup Guide](docs/SETUP.md) for full list.

---

## License

Project by SIVARAMAN R. All rights reserved.
