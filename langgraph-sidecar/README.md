# LangGraph Sidecar (Phase 2)
# Author: SIVARAMAN R <sivaram311@gmail.com>

## Run

```powershell
cd langgraph-sidecar
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python main.py
```

Health: http://localhost:8090/api/v1/health

## Enable in Spring Boot

```yaml
agent:
  langgraph:
    enabled: true
    service-url: http://localhost:8090
```

Or environment: `LANGGRAPH_ENABLED=true`

## Next steps

1. Add `agent_graph.py` with LangGraph ReAct workflow
2. Set `XAI_API_KEY` for Grok model
3. Register CLI tools that call cursor-agent via subprocess
4. Run as NSSM service alongside Spring Boot
