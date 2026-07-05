# PROJECT CONSCIOUSNESS

> **Single source of truth for any AI assistant working on this project.**  
> Read this file first before making changes.

---

## Identity

| Field | Value |
|-------|-------|
| **Project** | Persistent Agent Platform |
| **Author** | SIVARAMAN R |
| **Email** | sivaram311@gmail.com |
| **Repository** | https://github.com/sivaram311/persistent-agent-platform |
| **Local path** | `E:\MyWorkspace\persistent-agent-platform` |
| **Workspace root** | `E:\MyWorkspace` |
| **Version** | 0.2.0-SNAPSHOT |

---

## Last Activity

| Field | Value |
|-------|-------|
| **Date** | 2026-07-06 |
| **Last action** | Completed improvement items 1–4; Phase 5 & 6 documented as pending |
| **Next up** | Start LangGraph sidecar, deploy Dify, enable auth + NSSM for production |
| **App status** | Spring Boot on port 8080, PostgreSQL 18 `agent_platform` DB |

---

## What This Project Is

A **self-hosted, persistent AI agent platform** on Windows Server 2025 VPS. Spring Boot 3.4 + PostgreSQL 18 backend that unifies three agent solutions:

| Solution | Name | Status |
|----------|------|--------|
| 1 | CLI Wrapper (cursor-agent, agy, grok) | **Working** |
| 2 | LangGraph Python sidecar | **Scaffolded** (:8090 stub) |
| 3 | Dify.ai visual agent + RAG | **Scaffolded** (docker-compose) |

All conversation history and consciousness snapshots persist in PostgreSQL.

---

## What Has Been Done (Chronological)

### Session 1 — Git Backup (prior)
- Created `E:\GitBackup` — cloned 37 GitHub repos from `sivaram311`
- Consolidated into monorepo, pushed to `sivaram311/git-backup`
- Created `E:\GitBackup-archive.zip`

### Session 2 — Project creation
- Created `E:\MyWorkspace\persistent-agent-platform`
- Spring Boot scaffold: entities, repos, services, REST API, static chat UI
- Flyway V1 schema (6 tables + routing rules seed)
- Pushed public repo to GitHub

### Session 3 — Phase 1 completion
- PostgreSQL 18 setup: `agent_platform` DB, `agent_user`
- `application-local.yml` (gitignored) with DB creds + CLI paths
- Fixed Windows CLI: full paths, `cmd.exe /c` wrapper, `--trust` flag
- End-to-end chat verified via curl (~40s response time)
- Fixed WebController redirect `/` → `/index.html`
- Fixed legacy `/static/index.html` → `/index.html` redirect

### Session 4 — Documentation
- `docs/SETUP.md`, `docs/API.md`, `docs/ARCHITECTURE.md`, `docs/PROJECT_STATUS.md`
- `scripts/verify-api.ps1` — 6 automated curl tests (all passing)
- `scripts/curl-chat-test.json`, `scripts/start.ps1`, `scripts/setup-database.sql`

### Session 5 — Improvements (Phases 2–4 scaffold) ✅
- [x] `PROJECT_CONSCIOUSNESS.md` — master AI context file
- [x] `docs/PHASE_PENDING.md` — Phase 5 & 6 deferred scope
- [x] CLI audit logging — `CliExecutionLog` + `CliAuditService`
- [x] Consciousness prompt fix — CLI gets raw user message only
- [x] WebSocket `/ws/chat` + loading UI with stream log
- [x] LangGraph sidecar — `langgraph-sidecar/main.py` on :8090
- [x] Dify scaffold — `dify/docker-compose.yml` + `docs/DIFY_SETUP.md`
- [x] Phase 4 scripts — Spring Security, NSSM, Caddy, `docs/PRODUCTION.md`
- [x] External session history API endpoint

---

## Architecture (Quick Reference)

```
Browser (:8080)
    → AgentController / ChatWebSocketHandler
    → AgentOrchestratorService
        ├── ConsciousnessService  → PostgreSQL
        ├── HistoryService        → PostgreSQL
        ├── CliAuditService       → cli_execution_logs
        ├── CliAgentService       → cursor-agent / agy / grok
        ├── LangGraphAgentService → Python :8090
        └── DifyAgentService      → Dify :3000
```

---

## Key Files

| Path | Purpose |
|------|---------|
| `src/main/resources/application.yml` | Default config (committed) |
| `src/main/resources/application-local.yml` | Secrets + paths (NOT committed) |
| `src/main/resources/db/migration/V1__initial_schema.sql` | DB schema |
| `service/AgentOrchestratorService.java` | Central router |
| `service/cli/CliAgentService.java` | Subprocess CLI execution |
| `service/consciousness/ConsciousnessService.java` | Memory + snapshots |
| `scripts/verify-api.ps1` | curl test suite |
| `docs/ACTION_PLAN.md` | Full roadmap |
| `PROJECT_CONSCIOUSNESS.md` | **This file** |

---

## Database (PostgreSQL 18)

| Setting | Value |
|---------|-------|
| Host | localhost:5432 |
| Database | `agent_platform` |
| User | `agent_user` |
| Password | In `application-local.yml` (not in git) |
| Postgres superuser password | `postgres` (dev VPS) |

**Tables:** `agent_sessions`, `conversation_messages`, `consciousness_snapshots`, `memory_entries`, `cli_execution_logs`, `solution_routing_rules`

---

## CLI Paths (Windows VPS)

| CLI | Path |
|-----|------|
| Cursor Agent | `C:\Users\Administrator\AppData\Local\cursor-agent\cursor-agent.cmd` |
| Antigravity | `C:\Users\Administrator\AppData\Local\agy\bin\agy.exe` |
| Grok Build | Not installed / not in PATH |

---

## API Endpoints

| Method | Path | Auth |
|--------|------|------|
| GET | `/api/v1/health` | Public |
| POST | `/api/v1/chat` | Basic auth (Phase 4) |
| GET | `/api/v1/sessions/{id}/history` | Basic auth |
| GET | `/api/v1/sessions/external/{externalId}/history` | Basic auth |
| WS | `/ws/chat` | Basic auth |
| GET | `/index.html` | Public (UI prompts login) |

---

## Implementation Phases

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | Foundation — Spring Boot + PostgreSQL + CLI | **DONE** |
| 2 | LangGraph Python sidecar | **Scaffolded** — enable with `LANGGRAPH_ENABLED=true` |
| 3 | Dify Docker deployment | **Scaffolded** — see `docs/DIFY_SETUP.md` |
| 4 | Production — Auth + HTTPS + NSSM | **Scripts ready** — see `docs/PRODUCTION.md` |
| 5 | Enhancements — React UI, pgvector RAG, multi-user | **PENDING** → `docs/PHASE_PENDING.md` |
| 6 | Advanced scale — API keys, Prometheus, multi-CLI orchestration | **PENDING** → `docs/PHASE_PENDING.md` |

See `docs/ACTION_PLAN.md` and `docs/PHASE_PENDING.md` for Phase 5 & 6 details.

---

## Known Issues

1. Chat latency 30–60s (synchronous CLI) — WebSocket improves UX, not speed
2. Consciousness snapshots are text concatenation, not LLM summaries (Phase 5)
3. `memory_entries` table has no Java layer yet (Phase 5)
4. Grok CLI not installed
5. Never commit `application-local.yml`

---

## How to Run

```powershell
cd E:\MyWorkspace\persistent-agent-platform
.\scripts\start.ps1
# Verify
.\scripts\verify-api.ps1
```

---

## Conventions for AI Assistants

1. **Author attribution:** SIVARAMAN R / sivaram311@gmail.com in all new files
2. **No tool/agent references** in code comments or commits
3. **Update this file** after every significant change (Last Activity section)
4. **Do not commit** secrets or `application-local.yml`
5. **Match existing patterns:** Lombok, Spring Boot 3.4, Flyway migrations
6. **Test with** `scripts/verify-api.ps1` before marking work complete

---

## Related Projects on This VPS

| Path | Purpose |
|------|---------|
| `E:\MyWorkspace\persistent-agent-platform` | This project |
| `E:\GitBackup` | Monorepo backup of 37 GitHub repos |
| `E:\Source` | General dev workspace |

---

*Maintained by SIVARAMAN R — update after every session.*
