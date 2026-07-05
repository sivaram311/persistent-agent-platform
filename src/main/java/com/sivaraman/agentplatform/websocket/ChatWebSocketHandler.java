/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivaraman.agentplatform.dto.ChatRequest;
import com.sivaraman.agentplatform.dto.ChatResponse;
import com.sivaraman.agentplatform.dto.WebSocketChatMessage;
import com.sivaraman.agentplatform.service.AgentOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final AgentOrchestratorService orchestratorService;
    private final ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketChatMessage incoming = objectMapper.readValue(message.getPayload(), WebSocketChatMessage.class);

        if (!"chat".equals(incoming.getType())) {
            send(session, status("Unknown message type"));
            return;
        }

        send(session, status("Processing your request..."));

        ChatRequest request = new ChatRequest();
        request.setMessage(incoming.getMessage());
        request.setSessionId(incoming.getSessionId());
        request.setSolutionType(incoming.getSolutionType());
        request.setCliProvider(incoming.getCliProvider());

        ChatResponse response = orchestratorService.chat(
                request,
                status -> sendQuietly(session, status("status", status)),
                line -> sendQuietly(session, stream(line))
        );

        WebSocketChatMessage complete = new WebSocketChatMessage();
        complete.setType("complete");
        complete.setReply(response.getReply());
        complete.setExternalSessionId(response.getExternalSessionId());
        complete.setSolutionUsed(response.getSolutionUsed());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(complete)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("WebSocket closed: {}", status);
    }

    private WebSocketChatMessage status(String content) {
        return status("status", content);
    }

    private WebSocketChatMessage status(String type, String content) {
        WebSocketChatMessage msg = new WebSocketChatMessage();
        msg.setType(type);
        msg.setContent(content);
        return msg;
    }

    private WebSocketChatMessage stream(String line) {
        WebSocketChatMessage msg = new WebSocketChatMessage();
        msg.setType("stream");
        msg.setContent(line);
        return msg;
    }

    private void send(WebSocketSession session, WebSocketChatMessage msg) throws Exception {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
    }

    private void sendQuietly(WebSocketSession session, WebSocketChatMessage msg) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
        } catch (Exception ex) {
            log.warn("Failed to send WebSocket message", ex);
        }
    }
}
