/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.controller;

import com.sivaraman.agentplatform.dto.ChatRequest;
import com.sivaraman.agentplatform.dto.ChatResponse;
import com.sivaraman.agentplatform.dto.MessageDto;
import com.sivaraman.agentplatform.entity.AgentSession;
import com.sivaraman.agentplatform.repository.AgentSessionRepository;
import com.sivaraman.agentplatform.service.AgentOrchestratorService;
import com.sivaraman.agentplatform.service.history.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AgentController {

    private final AgentOrchestratorService orchestratorService;
    private final HistoryService historyService;
    private final AgentSessionRepository sessionRepository;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "platform", "Persistent Agent Platform",
                "author", "SIVARAMAN R"
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(orchestratorService.chat(request));
    }

    @GetMapping("/sessions/{sessionId}/history")
    public ResponseEntity<List<MessageDto>> history(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(historyService.getHistory(sessionId));
    }

    @GetMapping("/sessions/external/{externalId}/history")
    public ResponseEntity<List<MessageDto>> historyByExternalId(@PathVariable String externalId) {
        return sessionRepository.findByExternalId(externalId)
                .map(session -> ResponseEntity.ok(historyService.getHistory(session.getId())))
                .orElse(ResponseEntity.notFound().build());
    }
}
