# Architecture

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Last updated:** 2026-07-06

---

## System Overview

The Persistent Agent Platform is a unified backend that exposes three AI agent solutions through one REST API and web chat interface. All conversation data and consciousness state is stored in PostgreSQL.

```
┌─────────────────────────────────────────────────────────────────────┐
│                         BROWSER / API CLIENT                         │
│                    http://localhost:8080                             │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│              SPRING BOOT 3.4  (Java 21)  —  Port 8080               │
│                                                                      │
│  Controllers                                                         │
│  ├── AgentController      REST API (/api/v1/*)                      │
│  └── WebController        Redirect / → /index.html                  │
│                                                                      │
│  Services                                                            │
│  ├── AgentOrchestratorService   Routes requests to solutions        │
│  ├── ConsciousnessService       Builds context + snapshots          │
│  ├── HistoryService             Persists messages                   │
│  ├── CliAgentService            Solution 1 — subprocess CLIs        │
│  ├── LangGraphAgentService      Solution 2 — HTTP to Python         │
│  └── DifyAgentService           Solution 3 — HTTP to Dify           │
│                                                                      │
│  Data Layer                                                          │
│  ├── JPA Entities + Repositories                                    │
│  └── Flyway Migrations (V1)                                         │
└───────────────┬──────────────────────────────┬──────────────────────┘
                │                              │
                ▼                              ▼
┌───────────────────────────┐   ┌─────────────────────────────────────┐
│   PostgreSQL 18           │   │   AGENT EXECUTION LAYER              │
│   agent_platform DB       │   │                                      │
│                           │   │  Solution 1: CLI Wrapper             │
│  agent_sessions           │   │  ├── cursor-agent.cmd (Cursor)       │
│  conversation_messages    │   │  ├── agy.exe (Antigravity)           │
│  consciousness_snapshots  │   │  └── grok (Grok Build, optional)     │
│  memory_entries           │   │                                      │
│  cli_execution_logs       │   │  Solution 2: LangGraph Sidecar       │
│  solution_routing_rules   │   │  └── Python FastAPI :8090 (Phase 2)  │
└───────────────────────────┘   │                                      │
                                │  Solution 3: Dify.ai                 │
                                │  └── Docker :3000 (Phase 3)          │
                                └─────────────────────────────────────┘
                                                │
                                                ▼
                                ┌─────────────────────────────────────┐
                                │   FILE SANDBOX                       │
                                │   E:\MyWorkspace\                    │
                                └─────────────────────────────────────┘
```

---

## Package Structure

```
com.sivaraman.agentplatform
├── PersistentAgentPlatformApplication.java
├── config/
│   └── AgentProperties.java          ← @ConfigurationProperties
├── controller/
│   ├── AgentController.java          ← REST endpoints
│   └── WebController.java            ← UI redirect
├── domain/
│   ├── CliProvider.java              ← CURSOR, ANTIGRAVITY, GROK
│   ├── MessageRole.java              ← USER, ASSISTANT, SYSTEM, TOOL
│   └── SolutionType.java             ← CLI_WRAPPER, LANGGRAPH, DIFY
├── dto/
│   ├── ChatRequest.java
│   ├── ChatResponse.java
│   └── MessageDto.java
├── entity/
│   ├── AgentSession.java
│   ├── ConversationMessage.java
│   └── ConsciousnessSnapshot.java
├── repository/
│   ├── AgentSessionRepository.java
│   ├── ConversationMessageRepository.java
│   └── ConsciousnessSnapshotRepository.java
└── service/
    ├── AgentOrchestratorService.java ← Central router
    ├── cli/
    │   ├── CliAgentService.java      ← ProcessBuilder + Windows .cmd wrap
    │   └── CliExecutionResult.java
    ├── consciousness/
    │   └── ConsciousnessService.java ← Context building + snapshots
    ├── history/
    │   └── HistoryService.java       ← Message persistence
    ├── langgraph/
    │   └── LangGraphAgentService.java
    └── dify/
        └── DifyAgentService.java
```

---

## Request Flow (Chat)

```
1. POST /api/v1/chat { message, sessionId?, cliProvider?, solutionType? }
        │
2. AgentOrchestratorService.chat()
        ├── Resolve or create AgentSession (PostgreSQL)
        ├── ConsciousnessService.buildContextPrompt()  ← prior summary + recent messages
        ├── Build enriched prompt = consciousness + user message
        ├── Save USER message to conversation_messages
        ├── Route to solution:
        │     ├── CLI_WRAPPER  → CliAgentService.execute()
        │     ├── LANGGRAPH    → LangGraphAgentService.invoke()
        │     └── DIFY         → DifyAgentService.invoke()
        ├── Save ASSISTANT reply to conversation_messages
        ├── ConsciousnessService.maybeRefreshSnapshot()  ← every 30 messages
        └── Return ChatResponse with history
```

---

## Consciousness Model

The consciousness layer gives the agent persistent context without sending the entire conversation history on every request.

| Component | Storage | Purpose |
|-----------|---------|---------|
| Recent messages | `conversation_messages` | Last N messages injected into prompt |
| Consciousness snapshot | `consciousness_snapshots` | Rolling summary after every 30 messages |
| Long-term memory | `memory_entries` | Global/per-session facts (Phase 5: vector RAG) |

**Snapshot trigger:** `message_count % summary_threshold == 0` (default threshold: 30)

---

## Solution Routing

| Priority | Condition | Solution |
|----------|-----------|----------|
| Explicit | `solutionType` in request body | User-selected |
| Auto | Prompt contains "knowledge base" + Dify enabled | DIFY |
| Auto | Prompt contains "multi-step" + LangGraph enabled | LANGGRAPH |
| Default | All other requests | CLI_WRAPPER |

Rules are also stored in `solution_routing_rules` table for future dynamic configuration.

---

## CLI Execution (Solution 1)

`CliAgentService` spawns CLI agents via `ProcessBuilder`:

- **Windows `.cmd` files:** Wrapped with `cmd.exe /c`
- **Working directory:** Session `projectFolder` or `agent.workspace-root`
- **PATH inheritance:** System PATH passed to subprocess
- **Cursor flags:** `-p`, `--force`, `--trust`, optional `--resume {sessionId}`
- **Timeout:** 10 minutes per invocation

---

## Database Schema (Flyway V1)

See `src/main/resources/db/migration/V1__initial_schema.sql`.

---

## Planned Extensions

| Phase | Component | Port |
|-------|-----------|------|
| 2 | LangGraph Python sidecar | 8090 |
| 3 | Dify.ai (Docker/WSL2) | 3000 |
| 4 | Caddy reverse proxy | 443 |
| 5 | pgvector for semantic memory | — |

---

*Maintained by SIVARAMAN R (sivaram311@gmail.com)*
