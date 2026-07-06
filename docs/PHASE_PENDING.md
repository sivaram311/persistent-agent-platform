# Phase 5 & Phase 6 — Pending Implementation

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Status:** Deferred — implement after Phases 2, 3, and 4 are stable

---

## Phase 5 — Enhancements (Pending)

**Goal:** Rich UX, semantic memory, multi-user support.

| Task | Description | Priority |
|------|-------------|----------|
| React frontend | Replace static HTML with React chat UI, component library | High |
| WebSocket live CLI output | Stream stdout line-by-line from cursor-agent in browser | High |
| pgvector extension | Enable semantic search over `memory_entries` | High |
| Vector RAG | Embed documents/codebases for knowledge retrieval | Medium |
| Multi-user accounts | User table, per-user sessions and workspaces | Medium |
| Project switching UI | Select/create folders under `E:\MyWorkspace` | Medium |
| LLM consciousness summaries | Use xAI Grok to summarize instead of text concat | Medium |
| `memory_entries` Java layer | Entity, repository, service for long-term memory | Medium |
| File upload in chat UI | Attach files for agent context | Low |
| ~~Session list / history browser~~ | ~~UI to browse past conversations~~ | Done (sidebar) |

**Exit criteria:** React UI deployed, pgvector queries working, 2+ users with isolated sessions.

---

## Phase 6 — Advanced Scale (Pending)

**Goal:** Enterprise-grade operations, external integrations, observability.

| Task | Description | Priority |
|------|-------------|----------|
| API key authentication | External tools call `/api/v1/chat` with API keys | High |
| Multi-CLI orchestration | Auto-select Cursor vs Antigravity vs Grok per task type | Medium |
| Prometheus metrics | Request count, CLI duration, error rates | Medium |
| Grafana dashboard | Visual monitoring for agent usage | Medium |
| Rate limiting | Per-user/IP request limits | Medium |
| CLI approval gates | Confirm before destructive commands (delete, format) | High |
| Docker sandbox for CLI | Run agents in containers with volume mounts only | Medium |
| Horizontal scaling | Multiple Spring Boot instances + shared PostgreSQL | Low |
| Email/Slack notifications | Alert on agent errors or completed long tasks | Low |
| Backup automation | Scheduled `pg_dump` + offsite storage | Medium |

**Exit criteria:** Prometheus scraping metrics, API keys working, approval flow for destructive CLI ops.

---

## Why Deferred

Phases 2–4 must be stable first:

- **Phase 2** (LangGraph) — multi-step reasoning
- **Phase 3** (Dify) — RAG and visual workflows
- **Phase 4** (Auth + HTTPS + NSSM) — production safety

Phase 5 and 6 build on that foundation.

---

## When to Start Phase 5

Start when ALL of these are true:

- [ ] LangGraph sidecar running and tested
- [ ] Dify deployed (or consciously skipped with documented reason)
- [ ] Basic auth + HTTPS + NSSM service in production
- [ ] `verify-api.ps1` passes consistently
- [ ] At least 7 days stable uptime

---

## When to Start Phase 6

Start when Phase 5 React UI and pgvector RAG are working.

---

*Update PROJECT_CONSCIOUSNESS.md when Phase 5 or 6 begins.*
