"""
LangGraph ReAct agent orchestration.
Author: SIVARAMAN R <sivaram311@gmail.com>
"""
import os
from tools.cli_tool import run_coding_agent, list_workspace_files
from tools.web_search_tool import web_search

TOOLS = [run_coding_agent, list_workspace_files, web_search]


def run_agent(prompt: str, consciousness_context: str = "") -> str:
    api_key = os.getenv("XAI_API_KEY", "").strip()
    full_prompt = prompt
    if consciousness_context:
        full_prompt = f"{consciousness_context}\n\nUSER REQUEST:\n{prompt}"

    if api_key:
        return _run_langgraph_grok(full_prompt)
    return _run_fallback_orchestrator(full_prompt)


def _run_langgraph_grok(prompt: str) -> str:
    from langchain_xai import ChatXAI
    from langgraph.prebuilt import create_react_agent

    model_name = os.getenv("XAI_MODEL", "grok-2-1212")
    llm = ChatXAI(model=model_name, xai_api_key=os.getenv("XAI_API_KEY"))
    agent = create_react_agent(llm, TOOLS)

    result = agent.invoke({"messages": [("user", prompt)]})
    messages = result.get("messages", [])
    if messages:
        last = messages[-1]
        content = getattr(last, "content", str(last))
        return content if content else "Agent completed with no output."
    return "LangGraph agent returned no messages."


def _run_fallback_orchestrator(prompt: str) -> str:
    """Multi-step fallback when XAI_API_KEY is not set."""
    steps = []
    lower = prompt.lower()

    steps.append("Step 1 — Analyze workspace")
    files = list_workspace_files.invoke({})
    steps.append(files)

    if any(kw in lower for kw in ("search", "documentation", "docs", "what is", "how to")):
        steps.append("Step 2 — Web search")
        query = prompt[:200]
        steps.append(web_search.invoke({"query": query}))

    if any(kw in lower for kw in ("code", "fix", "bug", "implement", "refactor", "file", "edit")):
        steps.append("Step 3 — Run coding agent")
        steps.append(run_coding_agent.invoke({"task": prompt[:2000]}))
    else:
        steps.append("Step 3 — Summary")
        steps.append(
            "Task analyzed. Set XAI_API_KEY for full LangGraph + Grok orchestration. "
            "Coding step skipped (no code keywords detected)."
        )

    return "\n\n".join(steps)
