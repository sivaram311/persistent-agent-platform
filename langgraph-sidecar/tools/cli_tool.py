"""
CLI tool for LangGraph agent — invokes cursor-agent for coding tasks.
Author: SIVARAMAN R <sivaram311@gmail.com>
"""
import os
import subprocess
from langchain_core.tools import tool

WORKSPACE = os.getenv("WORKSPACE_ROOT", "E:/MyWorkspace/persistent-agent-platform")
CURSOR_AGENT = os.getenv(
    "CURSOR_AGENT_PATH",
    r"C:\Users\Administrator\AppData\Local\cursor-agent\cursor-agent.cmd",
)


def _run_cursor(prompt: str) -> str:
    cmd = [CURSOR_AGENT, "-p", prompt, "--force", "--trust"]
    if CURSOR_AGENT.lower().endswith((".cmd", ".bat")):
        cmd = ["cmd.exe", "/c"] + cmd
    try:
        result = subprocess.run(
            cmd,
            cwd=WORKSPACE,
            capture_output=True,
            text=True,
            timeout=600,
            env={**os.environ, "PATH": os.environ.get("PATH", "")},
        )
        output = (result.stdout or "").strip()
        if not output and result.stderr:
            output = result.stderr.strip()
        return output or f"CLI finished with exit code {result.returncode}"
    except subprocess.TimeoutExpired:
        return "CLI execution timed out after 10 minutes."
    except Exception as ex:
        return f"CLI error: {ex}"


@tool
def run_coding_agent(task: str) -> str:
    """Run the Cursor coding agent for code edits, debugging, or file operations."""
    return _run_cursor(task)


@tool
def list_workspace_files() -> str:
    """List top-level files and folders in the agent workspace."""
    try:
        entries = os.listdir(WORKSPACE)
        return "\n".join(sorted(entries)[:50])
    except Exception as ex:
        return f"Error listing workspace: {ex}"
