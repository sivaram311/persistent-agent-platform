/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.dify;

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
public class DifyAgentService {

    private final AgentProperties properties;
    private final RestClient restClient = RestClient.create();

    public String invoke(String sessionId, String prompt) {
        if (!properties.getDify().isEnabled()) {
            return "Dify service is disabled. Enable agent.dify.enabled=true to activate Solution 3.";
        }

        try {
            Map<String, Object> payload = Map.of(
                    "inputs", Map.of(),
                    "query", prompt,
                    "response_mode", "blocking",
                    "conversation_id", sessionId,
                    "user", "sivaram311"
            );

            Map<?, ?> response = restClient.post()
                    .uri(properties.getDify().getBaseUrl() + "/v1/chat-messages")
                    .header("Authorization", "Bearer " + properties.getDify().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.get("answer") != null) {
                return response.get("answer").toString();
            }
            return "Dify returned an empty response.";
        } catch (Exception ex) {
            log.error("Dify invocation failed", ex);
            return "Dify service unavailable: " + ex.getMessage();
        }
    }
}
