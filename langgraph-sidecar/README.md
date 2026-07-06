# LangGraph Sidecar (Phase 2 — Complete)
# Author: SIVARAMAN R <sivaram311@gmail.com>

## Features

- FastAPI service on port **8090**
- **Grok mode** — LangGraph ReAct agent with xAI API (`XAI_API_KEY` set)
- **Fallback mode** — Multi-step orchestrator (workspace list → web search → CLI) without API key
- Tools: `run_coding_agent`, `list_workspace_files`, `web_search`

## Setup

```powershell
cd langgraph-sidecar
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
Copy-Item .env.example .env
# Edit .env — set XAI_API_KEY for Grok mode
python main.py
```

Health: http://localhost:8090/api/v1/health

## Install as Windows Service

```powershell
# Run as Administrator
.\scripts\install-langgraph-service.ps1
```

## Enable in Spring Boot

```yaml
agent:
  langgraph:
    enabled: true
    service-url: http://localhost:8090
```

Or: `LANGGRAPH_ENABLED=true`

## API

```bash
curl.exe -s http://localhost:8090/api/v1/health
curl.exe -s -X POST http://localhost:8090/api/v1/agent/run \
  -H "Content-Type: application/json" \
  -d "{\"session_id\":\"test\",\"prompt\":\"List workspace and summarize project\"}"
```
