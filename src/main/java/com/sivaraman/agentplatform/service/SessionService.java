/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service;

import com.sivaraman.agentplatform.dto.SessionSummaryDto;
import com.sivaraman.agentplatform.entity.AgentSession;
import com.sivaraman.agentplatform.repository.AgentSessionRepository;
import com.sivaraman.agentplatform.repository.ConversationMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final AgentSessionRepository sessionRepository;
    private final ConversationMessageRepository messageRepository;

    public List<SessionSummaryDto> listRecentSessions(int limit) {
        return sessionRepository.findAllByOrderByLastActiveAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public void updateTitleFromFirstMessage(AgentSession session, String userMessage) {
        if (session.getTitle() == null || session.getTitle().startsWith("Session ")) {
            session.setTitle(truncateTitle(userMessage));
            sessionRepository.save(session);
        }
    }

    private SessionSummaryDto toSummary(AgentSession session) {
        return SessionSummaryDto.builder()
                .id(session.getId())
                .externalId(session.getExternalId())
                .title(session.getTitle())
                .solutionType(session.getSolutionType())
                .cliProvider(session.getCliProvider())
                .status(session.getStatus())
                .messageCount(messageRepository.countBySessionId(session.getId()))
                .createdAt(session.getCreatedAt())
                .lastActiveAt(session.getLastActiveAt())
                .build();
    }

    private String truncateTitle(String message) {
        if (message == null || message.isBlank()) {
            return "Untitled session";
        }
        String cleaned = message.strip().replaceAll("\\s+", " ");
        return cleaned.length() <= 60 ? cleaned : cleaned.substring(0, 57) + "...";
    }
}
