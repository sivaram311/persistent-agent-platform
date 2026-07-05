/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.history;

import com.sivaraman.agentplatform.domain.MessageRole;
import com.sivaraman.agentplatform.dto.MessageDto;
import com.sivaraman.agentplatform.entity.ConversationMessage;
import com.sivaraman.agentplatform.repository.ConversationMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ConversationMessageRepository messageRepository;

    @Transactional
    public ConversationMessage saveMessage(UUID sessionId, MessageRole role, String content) {
        ConversationMessage message = ConversationMessage.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .tokenCount(estimateTokens(content))
                .build();
        return messageRepository.save(message);
    }

    public List<MessageDto> getHistory(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(m -> MessageDto.builder()
                        .id(m.getId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }

    private int estimateTokens(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        return Math.max(1, content.split("\\s+").length);
    }
}
