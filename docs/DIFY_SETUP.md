# Dify Setup (Phase 3)

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Status:** Scaffold — requires Docker Desktop + WSL2

---

## Prerequisites

1. Enable WSL2 on Windows Server 2025
2. Install Docker Desktop
3. Obtain xAI API key for Grok model provider in Dify

---

## Deploy

```powershell
cd E:\MyWorkspace\persistent-agent-platform\dify
docker compose up -d
```

Access Dify UI: http://localhost:3000

---

## Wire to Spring Boot

```yaml
agent:
  dify:
    enabled: true
    base-url: http://localhost:3000
    api-key: YOUR_DIFY_API_KEY
```

Environment variables:
```
DIFY_ENABLED=true
DIFY_URL=http://localhost:3000
DIFY_API_KEY=your-key
```

---

## Note

The `docker-compose.yml` in this folder is a minimal scaffold. For production, use the [official Dify self-hosted guide](https://docs.dify.ai/getting-started/install-self-hosted) with full PostgreSQL, Redis, and Weaviate services.

---

*Phase 3 — pending full Docker deployment on VPS.*
