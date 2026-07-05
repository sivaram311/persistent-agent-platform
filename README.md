# Persistent Agent Platform

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Location:** `E:\MyWorkspace\persistent-agent-platform`  
**Version:** 0.1.0-SNAPSHOT

---

## Executive Summary

The **Persistent Agent Platform** is a self-hosted, always-on AI agent system designed for a **Windows Server 2025 VPS**. It unifies three complementary agent architectures into one production-grade Spring Boot application backed by **PostgreSQL**, providing **persistent conversation history** and a **consciousness layer** that maintains context across sessions.

| Goal | Approach |
|------|----------|
| Browser-accessible 24/7 agent | Spring Boot REST API + web chat UI on port 8080 |
| Use installed CLI agents | Solution 1 wraps Cursor Agent, Antigravity, Grok Build |
| Advanced orchestration | Solution 2 integrates LangGraph as a sidecar microservice |
| Visual agent builder | Solution 3 integrates Dify.ai via API |
| Persistent memory | PostgreSQL stores messages, sessions, consciousness snapshots |
| Unified tech stack | Java 21, Spring Boot 3.4, Flyway, PostgreSQL 16 |

### Why Spring Boot + PostgreSQL

Unlike a lightweight Python/Gradio wrapper alone, this stack provides:

- **Enterprise-grade persistence** — full audit trail of every message and CLI execution
- **Consciousness snapshots** — rolling summaries so the agent remembers goals and facts across long conversations
- **Multi-solution routing** — one API decides whether to use CLI, LangGraph, or Dify per request
- **Windows service ready** — runs as NSSM service alongside PostgreSQL
- **Extensible** — add auth, WebSocket streaming, RAG, and multi-user support incrementally

### Current Status

| Component | Status |
|-----------|--------|
| Spring Boot project scaffold | Done |
| PostgreSQL schema (Flyway V1) | Done |
| Solution 1 — CLI wrapper service | Done (skeleton) |
| Solution 2 — LangGraph integration | Done (API client, enable via config) |
| Solution 3 — Dify integration | Done (API client, enable via config) |
| Consciousness + history services | Done |
| Web chat UI | Done (static HTML) |
| LangGraph Python sidecar | Planned (Phase 2) |
| Dify Docker deployment | Planned (Phase 3) |
| NSSM + HTTPS (Caddy) | Planned (Phase 4) |

### Timeline Overview

| Phase | Duration | Deliverable |
|-------|----------|-------------|
| Phase 1 — Foundation | Week 1–2 | Spring Boot + PostgreSQL + CLI wrapper working end-to-end |
| Phase 2 — LangGraph | Week 3–4 | Python sidecar with xAI Grok API, multi-step workflows |
| Phase 3 — Dify | Week 5 | Docker/WSL2 Dify deployment, RAG knowledge bases |
| Phase 4 — Production | Week 6 | NSSM services, Caddy HTTPS, auth, monitoring |

---

## Quick Start

### Prerequisites

- Java 21, Maven 3.9+
- PostgreSQL 16+
- Cursor Agent CLI, Antigravity CLI (Grok optional)

### Database Setup

```sql
CREATE DATABASE agent_platform;
CREATE USER agent_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE agent_platform TO agent_user;
```

### Run

```powershell
cd E:\MyWorkspace\persistent-agent-platform
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

---

## Architecture

```
Browser (Chat UI)
       │
       ▼
Spring Boot API (:8080)
  ├── AgentOrchestratorService
  ├── ConsciousnessService  ──► PostgreSQL
  ├── HistoryService        ──► PostgreSQL
  ├── CliAgentService       ──► cursor-agent / agy / grok
  ├── LangGraphAgentService ──► Python sidecar (:8090)
  └── DifyAgentService      ──► Dify API (:3000)
       │
       ▼
E:\MyWorkspace  (agent file sandbox)
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Health check |
| POST | `/api/v1/chat` | Send message, get agent reply |
| GET | `/api/v1/sessions/{id}/history` | Retrieve conversation history |

---

## Configuration

See `src/main/resources/application.yml`. Key environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_NAME` | agent_platform | Database name |
| `DB_USER` | agent_user | Database user |
| `DB_PASSWORD` | changeme | Database password |
| `AGENT_WORKSPACE` | E:/MyWorkspace | Agent file sandbox |
| `LANGGRAPH_ENABLED` | false | Enable Solution 2 |
| `DIFY_ENABLED` | false | Enable Solution 3 |

---

## Documentation

- [Action Plan (full implementation roadmap)](docs/ACTION_PLAN.md)

---

## License

Private project by SIVARAMAN R. All rights reserved.
