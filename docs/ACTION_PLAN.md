# Action Plan — Persistent Agent Platform

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Project:** Persistent Agent Platform  
**Workspace:** `E:\MyWorkspace\persistent-agent-platform`  
**Date:** 2026-07-06  
**Status:** Phase 1 — Foundation (In Progress)

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

### Phase 1 — Foundation (Week 1–2) ✅ STARTED

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
| Install PostgreSQL on VPS | Pending | |
| End-to-end CLI test with cursor-agent | Pending | |
| WebSocket streaming for live output | Pending | |

**Exit criteria:** Send a message in browser → CLI runs → reply saved in PostgreSQL → consciousness snapshot created.

---

### Phase 2 — LangGraph Sidecar (Week 3–4)

**Goal:** Python microservice for multi-step agent workflows using xAI Grok API.

| Task | Details |
|------|---------|
| Create `langgraph-sidecar/` Python project | FastAPI + LangGraph + langchain-xai |
| Define ReAct agent graph | Planner → tool executor → reviewer nodes |
| Register CLI agents as tools | Spring Boot calls LangGraph; LangGraph calls CLIs for coding |
| Consciousness API | LangGraph receives consciousness context from Spring Boot |
| Enable via config | `agent.langgraph.enabled=true` |
| Docker option | Optional container for isolation |

**Directory structure:**
```
E:\MyWorkspace\persistent-agent-platform\
└── langgraph-sidecar\
    ├── requirements.txt
    ├── main.py
    ├── agent_graph.py
    └── tools\
        ├── cli_tool.py
        └── web_search_tool.py
```

**Exit criteria:** Multi-step task ("analyze codebase, fix bug, run tests") completes via LangGraph with history persisted in PostgreSQL.

---

### Phase 3 — Dify Deployment (Week 5)

**Goal:** Visual agent builder with RAG over documents and codebases.

| Task | Details |
|------|---------|
| Enable WSL2 on Windows Server 2025 | Required for Docker Desktop |
| Install Docker Desktop | Or use Docker in WSL2 directly |
| Deploy Dify via docker-compose | Official Dify self-hosted guide |
| Configure xAI Grok API key in Dify | Model provider setup |
| Create knowledge bases | Upload docs, codebase indexes |
| Wire DifyAgentService | Already scaffolded in Spring Boot |
| Enable via config | `agent.dify.enabled=true` |

**Exit criteria:** RAG query ("what does our auth module do?") routed to Dify, answer returned via Spring Boot API with history saved.

---

### Phase 4 — Production Hardening (Week 6)

**Goal:** 24/7 service, HTTPS, authentication, monitoring.

| Task | Details |
|------|---------|
| NSSM service for Spring Boot | Auto-start on boot |
| NSSM service for PostgreSQL | If not already a service |
| NSSM service for LangGraph sidecar | Phase 2 dependency |
| Caddy reverse proxy | Auto HTTPS for your domain |
| Or Cloudflare Tunnel | No open ports needed |
| Firewall rules | Allow 443 (Caddy) or block all (Cloudflare) |
| Authentication | Spring Security + JWT or basic auth |
| CLI sandboxing | Run CLI subprocess as low-privilege Windows user |
| Workspace isolation | Agent limited to E:\MyWorkspace subfolders |
| Approval gates | Require confirmation before destructive CLI commands |
| Logging + rotation | File logs via Logback, NSSM log rotation |
| Backup | pg_dump scheduled task for PostgreSQL |

**Exit criteria:** Agent accessible at `https://yourdomain.com`, authenticated, running 24/7, survives reboot.

---

### Phase 5 — Enhancements (Week 7+)

| Enhancement | Description |
|-------------|-------------|
| React frontend | Replace static HTML with rich chat UI, streaming, file upload |
| WebSocket streaming | Live terminal-like CLI output in browser |
| Vector RAG | pgvector extension for semantic memory search |
| Multi-user | User accounts, per-user sessions and workspaces |
| Project switching | UI to select/create project folders under MyWorkspace |
| Multi-CLI orchestration | Agent auto-selects best CLI for task type |
| API keys | External tools can call `/api/v1/chat` |
| Prometheus metrics | Grafana dashboard for agent usage |

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

1. Install and configure PostgreSQL on the VPS
2. Run `mvn spring-boot:run` and test `/api/v1/chat` with cursor-agent
3. Verify consciousness snapshots appear in PostgreSQL after 30 messages
4. Begin Phase 2 LangGraph sidecar Python project
5. Plan Dify Docker deployment (Phase 3)

---

*Document maintained by SIVARAMAN R (sivaram311@gmail.com)*
