"""
LangGraph Sidecar — Solution 2
Author: SIVARAMAN R <sivaram311@gmail.com>
"""
from fastapi import FastAPI
from pydantic import BaseModel
import os

app = FastAPI(title="LangGraph Agent Sidecar", version="0.1.0")


class AgentRunRequest(BaseModel):
    session_id: str
    prompt: str
    consciousness_context: str = ""


class AgentRunResponse(BaseModel):
    reply: str
    session_id: str


@app.get("/api/v1/health")
def health():
    return {"status": "UP", "author": "SIVARAMAN R", "service": "langgraph-sidecar"}


@app.post("/api/v1/agent/run", response_model=AgentRunResponse)
def run_agent(request: AgentRunRequest):
    """
    Phase 2 stub — replace with LangGraph ReAct agent + xAI Grok API.
    Set XAI_API_KEY in environment to enable full orchestration.
    """
    api_key = os.getenv("XAI_API_KEY", "")
    context_block = ""
    if request.consciousness_context:
        context_block = f"\n[Consciousness context]\n{request.consciousness_context}\n"

    if not api_key:
        reply = (
            f"LangGraph sidecar received your request.{context_block}\n"
            f"Prompt: {request.prompt[:500]}\n\n"
            "To enable full multi-step orchestration, set XAI_API_KEY and install "
            "langgraph + langchain-xai. See langgraph-sidecar/README.md."
        )
    else:
        reply = (
            f"LangGraph sidecar (XAI_API_KEY configured).{context_block}\n"
            f"Prompt received: {request.prompt[:300]}...\n\n"
            "Implement agent_graph.py with LangGraph workflow next."
        )

    return AgentRunResponse(reply=reply, session_id=request.session_id)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.getenv("PORT", "8090")))
