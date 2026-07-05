/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.langgraph;

import com.sivaraman.agentplatform.config.AgentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LangGraphAgentService {

    private final AgentProperties properties;
    private final RestClient restClient = RestClient.create();

    public String invoke(String sessionId, String prompt, String consciousnessContext) {
        if (!properties.getLanggraph().isEnabled()) {
            return "LangGraph service is disabled. Enable agent.langgraph.enabled=true to activate Solution 2.";
        }

        try {
            Map<String, Object> payload = Map.of(
                    "session_id", sessionId,
                    "prompt", prompt,
                    "consciousness_context", consciousnessContext
            );

            Map<?, ?> response = restClient.post()
                    .uri(properties.getLanggraph().getServiceUrl() + "/api/v1/agent/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.get("reply") != null) {
                return response.get("reply").toString();
            }
            return "LangGraph returned an empty response.";
        } catch (Exception ex) {
            log.error("LangGraph invocation failed", ex);
            return "LangGraph service unavailable: " + ex.getMessage();
        }
    }
}
