# Dify Setup (Phase 3 — Complete)

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Status:** Ready to deploy

---

## Prerequisites

1. **WSL2** enabled on Windows Server 2025
2. **Docker Desktop** installed and running
3. xAI API key for Grok model provider in Dify

---

## Deploy (automated)

```powershell
# Run as Administrator
cd E:\MyWorkspace\persistent-agent-platform
.\scripts\setup-dify.ps1
```

Or manually:

```powershell
cd dify
Copy-Item .env.example .env
docker compose up -d
```

---

## Services

| Service | Port | Purpose |
|---------|------|---------|
| dify-web | 3000 | Web UI + app builder |
| dify-api | 5001 | REST API |
| dify-db | internal | PostgreSQL |
| dify-redis | internal | Cache/queue |
| dify-worker | internal | Background jobs |

---

## Post-deploy configuration

1. Open **http://localhost:3000**
2. Create admin account (first visit)
3. **Settings → Model Provider → Add xAI Grok**
4. Create a **Chat App** or **Agent App**
5. Copy the app **API key** (starts with `app-`)

---

## Wire to Spring Boot

In `application-local.yml`:

```yaml
agent:
  dify:
    enabled: true
    base-url: http://localhost:3000
    api-key: app-your-key-here
```

Environment variables:
```
DIFY_ENABLED=true
DIFY_URL=http://localhost:3000
DIFY_API_KEY=app-your-key-here
```

Restart Spring Boot and select **Solution 3: Dify** in the chat UI.

---

## Verify

```bash
curl.exe -s http://localhost:3000
curl.exe -s http://localhost:5001/health
```

Send a RAG-style query via Spring Boot:
```json
{
  "message": "Search the knowledge base for authentication docs",
  "solutionType": "DIFY"
}
```

---

## Stop / remove

```powershell
cd dify
docker compose down
```

---

*Maintained by SIVARAMAN R*
