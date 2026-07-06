"""
Web search tool for LangGraph agent.
Author: SIVARAMAN R <sivaram311@gmail.com>
"""
import httpx
from langchain_core.tools import tool


@tool
def web_search(query: str) -> str:
    """Search the web for current information. Use for documentation, APIs, or recent events."""
    try:
        with httpx.Client(timeout=15.0) as client:
            resp = client.get(
                "https://api.duckduckgo.com/",
                params={"q": query, "format": "json", "no_redirect": 1},
            )
            data = resp.json()
            abstract = data.get("AbstractText") or data.get("Answer") or ""
            if abstract:
                return abstract
            related = data.get("RelatedTopics", [])[:3]
            snippets = [
                t.get("Text", "") for t in related if isinstance(t, dict) and t.get("Text")
            ]
            return "\n".join(snippets) if snippets else "No results found."
    except Exception as ex:
        return f"Search unavailable: {ex}"
