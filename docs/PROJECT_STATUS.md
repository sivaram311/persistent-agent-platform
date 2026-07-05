# Project Status

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Repository:** https://github.com/sivaram311/persistent-agent-platform  
**Last updated:** 2026-07-06

---

## Current Phase: Phase 1 Complete

---

## Work Completed

### Infrastructure

| Item | Status | Details |
|------|--------|---------|
| Project created | Done | `E:\MyWorkspace\persistent-agent-platform` |
| GitHub repo | Done | Public: `sivaram311/persistent-agent-platform` |
| PostgreSQL 18 database | Done | `agent_platform` on localhost:5432 |
| Database user | Done | `agent_user` |
| Flyway V1 migration | Done | 6 tables + routing seed data |

### Application (Spring Boot)

| Item | Status | Details |
|------|--------|---------|
| Spring Boot 3.4 scaffold | Done | Java 21, Maven |
| REST API | Done | health, chat, history |
| Web chat UI | Done | Static HTML at `/index.html` |
| Agent orchestrator | Done | Routes to 3 solutions |
| Consciousness service | Done | Context + snapshots |
| History service | Done | Full message persistence |
| CLI wrapper (Solution 1) | Done | Cursor + Antigravity + Grok paths |
| LangGraph client (Solution 2) | Done | HTTP client, disabled by default |
| Dify client (Solution 3) | Done | HTTP client, disabled by default |
| Local profile config | Done | `application-local.yml` (gitignored) |
| Start script | Done | `scripts/start.ps1` |
| DB setup script | Done | `scripts/setup-database.sql` |

### Git / Backup (prior work)

| Item | Status | Details |
|------|--------|---------|
| GitBackup monorepo | Done | 37 repos consolidated at `E:\GitBackup` |
| GitHub git-backup repo | Done | Private monorepo snapshot |

---

## curl Verification (2026-07-06)

All tests run against `http://localhost:8080`:

| Test | Command | Result |
|------|---------|--------|
| Health | `curl.exe -s http://localhost:8080/api/v1/health` | `{"status":"UP","author":"SIVARAMAN R",...}` |
| Root redirect | `GET /` | HTTP 302 → `/index.html` |
| Chat UI | `GET /index.html` | HTTP 200 |
| Actuator | `GET /actuator/health` | HTTP 200 |
| Chat API | `POST /api/v1/chat` with JSON file | HTTP 200, session + history returned (~36s) |
| History API | `GET /api/v1/sessions/{uuid}/history` | HTTP 200, message array returned |

Run automated verification:

```powershell
.\scripts\verify-api.ps1
```

---

## PostgreSQL Data (live)

After verification runs:

- **Sessions:** 5+
- **Messages:** 10+
- **Tables:** 7 (including flyway_schema_history)

---

## Known Issues / Notes

| Issue | Status | Notes |
|-------|--------|-------|
| Grok CLI not in PATH | Open | Optional; use Cursor or Antigravity |
| Chat latency 30–60s | Expected | Synchronous CLI execution |
| Consciousness on first message | Minor | No snapshot until 30 messages |
| `/static/index.html` | Fixed | Redirect now points to `/index.html` |
| PowerShell curl JSON escaping | Documented | Use `--data-binary "@file.json"` |

---

## Next Steps (Phase 2)

1. Build LangGraph Python sidecar in `langgraph-sidecar/`
2. Implement FastAPI `/api/v1/agent/run` endpoint
3. Enable `LANGGRAPH_ENABLED=true`
4. Test multi-step orchestration via Spring Boot

---

## Commit History

| Commit | Description |
|--------|-------------|
| `8e44a3f` | Initial scaffold — entities, services, UI, action plan |
| `9f63626` | Phase 1 — PostgreSQL setup, CLI Windows fixes, scripts |
| (pending) | Documentation + curl verification + UI redirect fix |

---

*Maintained by SIVARAMAN R (sivaram311@gmail.com)*
