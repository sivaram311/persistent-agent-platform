"""
LangGraph Sidecar — Solution 2 (Complete)
Author: SIVARAMAN R <sivaram311@gmail.com>
"""
from fastapi import FastAPI
from pydantic import BaseModel
from dotenv import load_dotenv
import os

load_dotenv()

from agent_graph import run_agent

app = FastAPI(title="LangGraph Agent Sidecar", version="0.2.0")


class AgentRunRequest(BaseModel):
    session_id: str
    prompt: str
    consciousness_context: str = ""


class AgentRunResponse(BaseModel):
    reply: str
    session_id: str
    mode: str


@app.get("/api/v1/health")
def health():
    has_key = bool(os.getenv("XAI_API_KEY", "").strip())
    return {
        "status": "UP",
        "author": "SIVARAMAN R",
        "service": "langgraph-sidecar",
        "xai_configured": has_key,
        "mode": "grok-react" if has_key else "fallback-orchestrator",
    }


@app.post("/api/v1/agent/run", response_model=AgentRunResponse)
def run_agent_endpoint(request: AgentRunRequest):
    mode = "grok-react" if os.getenv("XAI_API_KEY", "").strip() else "fallback-orchestrator"
    reply = run_agent(request.prompt, request.consciousness_context)
    return AgentRunResponse(reply=reply, session_id=request.session_id, mode=mode)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.getenv("PORT", "8090")))
