/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.consciousness;

import com.sivaraman.agentplatform.config.AgentProperties;
import com.sivaraman.agentplatform.domain.MessageRole;
import com.sivaraman.agentplatform.entity.ConsciousnessSnapshot;
import com.sivaraman.agentplatform.entity.ConversationMessage;
import com.sivaraman.agentplatform.repository.ConsciousnessSnapshotRepository;
import com.sivaraman.agentplatform.repository.ConversationMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsciousnessService {

    private final ConversationMessageRepository messageRepository;
    private final ConsciousnessSnapshotRepository snapshotRepository;
    private final AgentProperties properties;

    public String buildContextPrompt(UUID sessionId) {
        StringBuilder context = new StringBuilder();

        snapshotRepository.findFirstBySessionIdOrderByVersionDesc(sessionId).ifPresent(snapshot -> {
            context.append("=== CONSCIOUSNESS SUMMARY ===\n");
            context.append(snapshot.getSummary()).append("\n");
            if (snapshot.getKeyFacts() != null && !snapshot.getKeyFacts().isEmpty()) {
                context.append("Key facts: ").append(String.join("; ", snapshot.getKeyFacts())).append("\n");
            }
            if (snapshot.getActiveGoals() != null && !snapshot.getActiveGoals().isEmpty()) {
                context.append("Active goals: ").append(String.join("; ", snapshot.getActiveGoals())).append("\n");
            }
            context.append("\n");
        });

        List<ConversationMessage> recent = getRecentMessages(sessionId);
        if (!recent.isEmpty()) {
            context.append("=== RECENT CONVERSATION ===\n");
            for (ConversationMessage msg : recent) {
                context.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
        }

        return context.toString();
    }

    @Transactional
    public void maybeRefreshSnapshot(UUID sessionId) {
        long count = messageRepository.countBySessionId(sessionId);
        int threshold = properties.getConsciousness().getSummaryThresholdMessages();

        if (count >= 2 && (count <= 2 || count % threshold == 0)) {
            List<ConversationMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            String summary = summarizeMessages(messages);
            int nextVersion = snapshotRepository.findFirstBySessionIdOrderByVersionDesc(sessionId)
                    .map(s -> s.getVersion() + 1)
                    .orElse(1);

            ConsciousnessSnapshot snapshot = ConsciousnessSnapshot.builder()
                    .sessionId(sessionId)
                    .summary(summary)
                    .keyFacts(extractKeyFacts(messages))
                    .activeGoals(new ArrayList<>())
                    .messageCount((int) count)
                    .version(nextVersion)
                    .build();

            snapshotRepository.save(snapshot);
        }
    }

    public String latestSummary(UUID sessionId) {
        return snapshotRepository.findFirstBySessionIdOrderByVersionDesc(sessionId)
                .map(ConsciousnessSnapshot::getSummary)
                .orElse("No consciousness snapshot yet.");
    }

    private List<ConversationMessage> getRecentMessages(UUID sessionId) {
        List<ConversationMessage> all = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        int max = properties.getConsciousness().getMaxContextMessages();
        if (all.size() <= max) {
            return all;
        }
        return all.subList(all.size() - max, all.size());
    }

    private String summarizeMessages(List<ConversationMessage> messages) {
        return messages.stream()
                .filter(m -> m.getRole() == MessageRole.USER || m.getRole() == MessageRole.ASSISTANT)
                .map(m -> m.getRole() + ": " + truncate(m.getContent(), 200))
                .collect(Collectors.joining(" | "));
    }

    private List<String> extractKeyFacts(List<ConversationMessage> messages) {
        return messages.stream()
                .filter(m -> m.getRole() == MessageRole.USER)
                .map(m -> truncate(m.getContent(), 120))
                .limit(5)
                .toList();
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max) + "...";
    }
}
