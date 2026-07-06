# API Reference

**Author:** SIVARAMAN R  
**Email:** sivaram311@gmail.com  
**Base URL:** `http://localhost:8080`  
**Last verified:** 2026-07-06

---

## Endpoints Summary

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Platform health check |
| GET | `/api/v1/sessions` | List recent sessions |
| POST | `/api/v1/chat` | Send message to agent |
| GET | `/api/v1/sessions/{sessionId}/history` | Get conversation history |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/` | Redirect to chat UI |
| GET | `/index.html` | Chat UI |

---

## 1. Health Check

### Request

```bash
curl.exe -s http://localhost:8080/api/v1/health
```

### Response (200 OK)

```json
{
  "status": "UP",
  "author": "SIVARAMAN R",
  "platform": "Persistent Agent Platform"
}
```

### Verified result (2026-07-06)

```
{"status":"UP","author":"SIVARAMAN R","platform":"Persistent Agent Platform"}
```

---

## 2. List Sessions

Returns recent sessions for the sidebar, ordered by last activity.

### Request

```bash
curl.exe -s "http://localhost:8080/api/v1/sessions?limit=50"
```

### Response (200 OK)

```json
[
  {
    "id": "63782e06-c165-47c7-ba55-3743e595a531",
    "externalId": "94e156eb-b0be-4c04-9d3c-7561b3138a6b",
    "title": "Reply with exactly: OK",
    "solutionType": "CLI_WRAPPER",
    "cliProvider": "CURSOR",
    "status": "ACTIVE",
    "messageCount": 2,
    "createdAt": "2026-07-05T21:34:56Z",
    "lastActiveAt": "2026-07-05T21:35:29Z"
  }
]
```

---

## 3. Chat

Send a message to the agent. The platform routes to Solution 1 (CLI), Solution 2 (LangGraph), or Solution 3 (Dify) based on request parameters and configuration.

### Request

```bash
curl.exe -s -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  --data-binary "@scripts/curl-chat-test.json"
```

**Request body:**

```json
{
  "message": "Reply with exactly: OK",
  "sessionId": null,
  "cliProvider": "CURSOR",
  "solutionType": "CLI_WRAPPER",
  "projectFolder": null
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `message` | string | Yes | User prompt |
| `sessionId` | string | No | External session ID for continuity |
| `cliProvider` | enum | No | `CURSOR`, `ANTIGRAVITY`, `GROK` |
| `solutionType` | enum | No | `CLI_WRAPPER`, `LANGGRAPH`, `DIFY` |
| `projectFolder` | string | No | Working directory override |

### Response (200 OK)

```json
{
  "sessionId": "506558d9-6f29-4def-b318-be98c8c860e1",
  "externalSessionId": "94e156eb-b0be-4c04-9d3c-7561b3138a6b",
  "reply": "...",
  "solutionUsed": "CLI_WRAPPER",
  "consciousnessSummary": "No consciousness snapshot yet.",
  "history": [
    {
      "id": "7efeb76a-8fe8-440d-9dc5-2eeda8e3501d",
      "role": "USER",
      "content": "Reply with exactly: OK",
      "createdAt": "2026-07-05T21:34:56.004762Z"
    },
    {
      "id": "80b9a52e-69f4-40d7-a187-57af3b51044f",
      "role": "ASSISTANT",
      "content": "...",
      "createdAt": "2026-07-05T21:35:29.702983Z"
    }
  ],
  "timestamp": "2026-07-05T21:35:29.715026Z"
}
```

### Notes

- First request creates a new session; use `externalSessionId` in subsequent requests for continuity.
- CLI responses typically take **30–60 seconds**.
- On Windows, use `--data-binary "@file.json"` to avoid JSON escaping issues in PowerShell.

### Continue a session

```bash
curl.exe -s -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"What did I ask before?\",\"sessionId\":\"94e156eb-b0be-4c04-9d3c-7561b3138a6b\",\"cliProvider\":\"CURSOR\",\"solutionType\":\"CLI_WRAPPER\"}"
```

---

## 4. Session History

Retrieve all messages for a session using the internal UUID (`sessionId` from chat response, not `externalSessionId`).

### Request

```bash
curl.exe -s http://localhost:8080/api/v1/sessions/506558d9-6f29-4def-b318-be98c8c860e1/history
```

### Response (200 OK)

```json
[
  {
    "id": "7efeb76a-8fe8-440d-9dc5-2eeda8e3501d",
    "role": "USER",
    "content": "Reply with exactly: OK",
    "createdAt": "2026-07-05T21:34:56.004762Z"
  },
  {
    "id": "80b9a52e-69f4-40d7-a187-57af3b51044f",
    "role": "ASSISTANT",
    "content": "...",
    "createdAt": "2026-07-05T21:35:29.702983Z"
  }
]
```

---

## 4. Actuator Health

```bash
curl.exe -s http://localhost:8080/actuator/health
```

Returns Spring Boot actuator health (HTTP 200 when app and DB are connected).

---

## 5. Web UI

```bash
curl.exe -s -o NUL -w "HTTP %{http_code}\n" http://localhost:8080/
curl.exe -s -o NUL -w "HTTP %{http_code}\n" http://localhost:8080/index.html
```

| URL | Expected |
|-----|----------|
| `/` | HTTP 302 → `/index.html` |
| `/index.html` | HTTP 200 |

---

## Error Responses

| Status | Cause |
|--------|-------|
| 400 | Invalid JSON or missing `message` field |
| 404 | Session not found (history endpoint) |
| 500 | Database or CLI execution failure |

---

*Maintained by SIVARAMAN R (sivaram311@gmail.com)*
