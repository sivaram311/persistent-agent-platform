# Verify API with curl
# Author: SIVARAMAN R <sivaram311@gmail.com>

$BaseUrl = "http://localhost:8080"
$Passed = 0
$Failed = 0

function Test-Endpoint {
    param($Name, $ScriptBlock)
    Write-Host -NoNewline "  $Name ... "
    try {
        & $ScriptBlock
        Write-Host "PASS" -ForegroundColor Green
        $script:Passed++
    } catch {
        Write-Host "FAIL: $($_.Exception.Message)" -ForegroundColor Red
        $script:Failed++
    }
}

Write-Host ""
Write-Host "Persistent Agent Platform - API Verification" -ForegroundColor Cyan
Write-Host "Author: SIVARAMAN R | Base URL: $BaseUrl"
Write-Host ""

Test-Endpoint "Health check" {
    $r = curl.exe --max-time 10 -s "$BaseUrl/api/v1/health" | ConvertFrom-Json
    if ($r.status -ne "UP") { throw "status=$($r.status)" }
}

Test-Endpoint "Root redirect (302)" {
    curl.exe --max-time 10 -s -o NUL -w "%{http_code}" "$BaseUrl/" | ForEach-Object {
        if ($_ -ne "302") { throw "HTTP $_" }
    }
}

Test-Endpoint "Chat UI (200)" {
    curl.exe --max-time 10 -s -o NUL -w "%{http_code}" "$BaseUrl/index.html" | ForEach-Object {
        if ($_ -ne "200") { throw "HTTP $_" }
    }
}

Test-Endpoint "Actuator health (200)" {
    curl.exe --max-time 10 -s -o NUL -w "%{http_code}" "$BaseUrl/actuator/health" | ForEach-Object {
        if ($_ -ne "200") { throw "HTTP $_" }
    }
}

Test-Endpoint "Chat API (POST)" {
    $jsonFile = Join-Path $PSScriptRoot "curl-chat-test.json"
    $raw = curl.exe --max-time 90 -s -X POST "$BaseUrl/api/v1/chat" `
        -H "Content-Type: application/json" `
        --data-binary "@$jsonFile"
    $r = $raw | ConvertFrom-Json
    if (-not $r.sessionId) { throw "No sessionId in response" }
    if (-not $r.reply) { throw "No reply in response" }
    $script:SessionId = $r.sessionId
    Write-Host "`n    sessionId: $($r.sessionId)" -ForegroundColor DarkGray
    Write-Host "    solution:  $($r.solutionUsed)" -ForegroundColor DarkGray
}

if ($SessionId) {
    Test-Endpoint "Session history (GET)" {
        $raw = curl.exe --max-time 10 -s "$BaseUrl/api/v1/sessions/$SessionId/history"
        $history = $raw | ConvertFrom-Json
        if ($history.Count -lt 2) { throw "Expected >= 2 messages, got $($history.Count)" }
    }
}

Write-Host ""
Write-Host "Results: $Passed passed, $Failed failed" -ForegroundColor Cyan
if ($Failed -gt 0) { exit 1 }
