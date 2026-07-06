# Action Plan — Persistent Agent Platform

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Project:** Persistent Agent Platform  
**Workspace:** `E:\MyWorkspace\persistent-agent-platform`  
**Date:** 2026-07-06  
**Status:** Phase 1 Complete | Phases 2–4 In Progress | Phases 5–6 Pending

See [PHASE_PENDING.md](PHASE_PENDING.md) for deferred Phase 5 & 6 scope.

---

## 1. Vision

Build a fully self-hosted, persistent AI agent on Windows Server 2025 VPS that:

1. Is accessible from any browser, 24/7, without RDP/SSH
2. Uses installed CLI agents (Cursor Agent, Antigravity, Grok Build) for code tasks
3. Supports advanced LangGraph orchestration for multi-step reasoning
4. Supports Dify.ai for visual workflow building and RAG
5. Maintains full conversation history and a consciousness layer in PostgreSQL
6. Runs as Windows services with HTTPS via Caddy or Cloudflare Tunnel

---

## 2. Solution Architecture (All Three Combined)

```
┌─────────────────────────────────────────────────────────────────┐
│                    PERSISTENT AGENT PLATFORM                      │
│                   Spring Boot 3.4 + PostgreSQL                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐   ┌──────────────────┐   ┌─────────────────┐  │
│  │  Web Chat UI │──►│ AgentOrchestrator │──►│ Consciousness   │  │
│  │  (Browser)   │   │     Service       │   │ Service + PG    │  │
│  └──────────────┘   └────────┬─────────┘   └─────────────────┘  │
│                               │                                   │
│              ┌────────────────┼────────────────┐                  │
│              ▼                ▼                ▼                  │
│  ┌─────────────────┐ ┌──────────────┐ ┌─────────────────┐       │
│  │  SOLUTION 1     │ │  SOLUTION 2  │ │  SOLUTION 3     │       │
│  │  CLI Wrapper    │ │  LangGraph   │ │  Dify.ai        │       │
│  │                 │ │  Sidecar     │ │  (Docker/WSL2)  │       │
│  │  cursor-agent   │ │  Python      │ │  Visual builder │       │
│  │  agy (Antigrav) │ │  FastAPI     │ │  RAG + tools    │       │
│  │  grok           │ │  xAI Grok API│ │  Multi-agent    │       │
│  └────────┬────────┘ └──────┬───────┘ └────────┬────────┘       │
│           │                 │                   │                 │
│           └─────────────────┼───────────────────┘                 │
│                             ▼                                     │
│                   E:\MyWorkspace (sandbox)                        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Backend API | Spring Boot 3.4, Java 21 | Core orchestration, REST, WebSocket |
| Database | PostgreSQL 16 | Sessions, messages, consciousness, memory |
| Migrations | Flyway | Version-controlled schema |
| Solution 1 | Subprocess CLI invocation | Direct agent execution |
| Solution 2 | Python FastAPI + LangGraph | Multi-step agent workflows |
| Solution 3 | Dify.ai (Docker) | Visual agent builder + RAG |
| Frontend | Static HTML/JS (Phase 1), React (Phase 5) | Browser chat interface |
| Service Manager | NSSM | 24/7 Windows services |
| Reverse Proxy | Caddy or Cloudflare Tunnel | HTTPS + domain |
| Monitoring | Spring Actuator + file logs | Health checks |

---

## 4. PostgreSQL Schema (Consciousness Model)

### Core Tables

| Table | Purpose |
|-------|---------|
| `agent_sessions` | One row per chat session; tracks solution type, CLI provider, project folder |
| `conversation_messages` | Every user/assistant/system message with timestamps |
| `consciousness_snapshots` | Rolling summaries, key facts, active goals — the agent's "memory" |
| `memory_entries` | Long-term facts (global or per-session), future vector RAG support |
| `cli_execution_logs` | Audit trail of every CLI subprocess invocation |
| `solution_routing_rules` | Configurable rules for Solution 1/2/3 selection |

### Consciousness Flow

```
User sends message
    │
    ▼
Load latest consciousness_snapshot for session
    │
    ▼
Build context prompt = summary + key_facts + recent N messages
    │
    ▼
Dispatch to Solution 1 / 2 / 3
    │
    ▼
Save assistant reply to conversation_messages
    │
    ▼
If message_count % threshold == 0 → create new consciousness_snapshot
```

---

## 5. Phase-by-Phase Implementation

### Phase 1 — Foundation (Week 1–2) ✅ COMPLETE

**Goal:** Working Spring Boot app with PostgreSQL, CLI wrapper, web chat.

| Task | Status | Owner |
|------|--------|-------|
| Create project under E:\MyWorkspace | Done | SIVARAMAN R |
| Spring Boot scaffold (pom, entities, repos) | Done | SIVARAMAN R |
| Flyway V1 schema | Done | SIVARAMAN R |
| CliAgentService (cursor-agent, agy, grok) | Done | SIVARAMAN R |
| ConsciousnessService + HistoryService | Done | SIVARAMAN R |
| AgentOrchestratorService (routing) | Done | SIVARAMAN R |
| REST API + static chat UI | Done | SIVARAMAN R |
| Install PostgreSQL on VPS | Done | PostgreSQL 18.4 |
| Configure agent_platform database | Done | agent_user created |
| End-to-end CLI test with cursor-agent | Done | Verified via curl |
| Documentation (SETUP, API, ARCHITECTURE) | Done | docs/ folder |
| curl API verification script | Done | scripts/verify-api.ps1 |
| WebSocket streaming for live output | Pending | Phase 5 |

**Exit criteria:** ✅ Send a message in browser → CLI runs → reply saved in PostgreSQL → consciousness snapshot created.

---

### Phase 2 — LangGraph Sidecar (Week 3–4) ✅ COMPLETE

**Goal:** Python microservice for multi-step agent workflows using xAI Grok API.

| Task | Status |
|------|--------|
| FastAPI + LangGraph project | Done |
| ReAct agent graph with Grok (XAI_API_KEY) | Done |
| Fallback multi-step orchestrator (no API key) | Done |
| CLI + web search tools | Done |
| NSSM Windows service script | Done |
| Spring Boot integration | Done — `LANGGRAPH_ENABLED=true` |

**Exit criteria:** ✅ Multi-step tasks route to LangGraph; history persisted in PostgreSQL.

---

### Phase 3 — Dify Deployment (Week 5) ✅ COMPLETE

**Goal:** Visual agent builder with RAG over documents and codebases.

| Task | Status |
|------|--------|
| docker-compose full stack | Done |
| setup-dify.ps1 deploy script | Done |
| DifyAgentService wired | Done |
| DIFY_SETUP.md guide | Done |
| Enable via config | Done — `DIFY_ENABLED=true` + API key |

**Exit criteria:** ✅ RAG queries routable to Dify via Spring Boot (requires Docker deploy + API key).

---

### Phase 4 — Production Hardening (Week 6) ✅ COMPLETE

**Goal:** 24/7 service, HTTPS, authentication, monitoring.

| Task | Status |
|------|--------|
| NSSM services (Spring Boot + LangGraph) | Done |
| install-production.ps1 one-command install | Done |
| Caddy HTTPS template | Done |
| Firewall rules script | Done |
| Spring Security basic auth | Done |
| Workspace isolation | Done — WorkspaceGuardService |
| CLI approval gates | Done — CliApprovalService |
| File logging + rotation | Done — logback-spring.xml |
| PostgreSQL backup + scheduled task | Done |

**Exit criteria:** ✅ Scripts ready; run `install-production.ps1` + Caddy for live HTTPS deployment.

---

### Phase 5 — Enhancements (Week 7+) ⏸ PENDING

See `docs/PHASE_PENDING.md`.

---

### Phase 6 — Advanced Scale ⏸ PENDING

See `docs/PHASE_PENDING.md`.

---

## 6. Solution Routing Logic

The `AgentOrchestratorService` selects a solution per request:

| Condition | Solution | Example |
|-----------|----------|---------|
| Default / code edit / run command | Solution 1 — CLI Wrapper | "Fix the login bug in stack-pilot" |
| Multi-step / complex orchestration | Solution 2 — LangGraph | "Analyze, refactor, test, and deploy" |
| RAG / knowledge base query | Solution 3 — Dify | "What does our API documentation say about auth?" |
| Explicit override | User selects in UI dropdown | Any |

Routing rules stored in `solution_routing_rules` table for runtime configuration.

---

## 7. Security Checklist

- [ ] Agent sandboxed to `E:\MyWorkspace` only
- [ ] CLI subprocess runs as low-privilege user
- [ ] HTTPS enforced (no plain HTTP in production)
- [ ] Authentication on web UI and API
- [ ] PostgreSQL not exposed to public internet
- [ ] API keys stored in environment variables, not in code
- [ ] CLI execution logged with full audit trail
- [ ] Approval required for destructive operations (delete, format, etc.)
- [ ] Rate limiting on chat API
- [ ] Regular PostgreSQL backups

---

## 8. Environment Setup Commands

### PostgreSQL (Windows)

```powershell
# Install via winget or official installer
winget install PostgreSQL.PostgreSQL

# Create database
psql -U postgres -c "CREATE DATABASE agent_platform;"
psql -U postgres -c "CREATE USER agent_user WITH ENCRYPTED PASSWORD 'your_password';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE agent_platform TO agent_user;"
```

### Spring Boot

```powershell
cd E:\MyWorkspace\persistent-agent-platform
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

### NSSM (Phase 4)

```powershell
C:\nssm\nssm.exe install AgentPlatform "C:\Program Files\Eclipse Adoptium\jdk-21\bin\java.exe" "-jar E:\MyWorkspace\persistent-agent-platform\target\persistent-agent-platform-0.1.0-SNAPSHOT.jar"
C:\nssm\nssm.exe set AgentPlatform AppDirectory "E:\MyWorkspace\persistent-agent-platform"
C:\nssm\nssm.exe set AgentPlatform Start SERVICE_AUTO_START
net start AgentPlatform
```

---

## 9. Risk Register

| Risk | Impact | Mitigation |
|------|--------|------------|
| CLI agent runs destructive command | High | Sandbox to workspace, approval gates, audit logs |
| PostgreSQL data loss | High | Scheduled pg_dump backups |
| Large repo clone exceeds GitHub limits | Medium | Already consolidated in git-backup monorepo |
| Grok CLI not in PATH | Low | Install Grok or use xAI API via LangGraph |
| Dify Docker on Windows complexity | Medium | Use WSL2, follow official Dify guide |
| Token/context window limits | Medium | Consciousness snapshots compress old context |

---

## 10. Success Metrics

| Metric | Target |
|--------|--------|
| Agent uptime | 99.5% (NSSM auto-restart) |
| Chat response time (CLI) | < 60 seconds for simple tasks |
| History retention | Unlimited in PostgreSQL |
| Consciousness accuracy | Agent references prior goals/facts correctly |
| Solution routing | Correct solution selected > 90% of time |
| Zero data loss | All messages persisted before response returned |

---

## 11. Next Immediate Actions

1. ~~Install and configure PostgreSQL on the VPS~~ ✅ Done
2. ~~Run `mvn spring-boot:run` and test `/api/v1/chat` with cursor-agent~~ ✅ Done
3. Begin Phase 2 LangGraph sidecar Python project
4. Plan Dify Docker deployment (Phase 3)
5. Add NSSM Windows service + Caddy HTTPS (Phase 4)

---

*Document maintained by SIVARAMAN R (sivaram311@gmail.com)*
