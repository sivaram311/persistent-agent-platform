/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service;

import com.sivaraman.agentplatform.config.AgentProperties;
import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.domain.MessageRole;
import com.sivaraman.agentplatform.domain.SolutionType;
import com.sivaraman.agentplatform.dto.ChatRequest;
import com.sivaraman.agentplatform.dto.ChatResponse;
import com.sivaraman.agentplatform.entity.AgentSession;
import com.sivaraman.agentplatform.repository.AgentSessionRepository;
import com.sivaraman.agentplatform.service.cli.CliAgentService;
import com.sivaraman.agentplatform.service.cli.CliAuditService;
import com.sivaraman.agentplatform.service.cli.CliExecutionResult;
import com.sivaraman.agentplatform.service.consciousness.ConsciousnessService;
import com.sivaraman.agentplatform.service.dify.DifyAgentService;
import com.sivaraman.agentplatform.service.history.HistoryService;
import com.sivaraman.agentplatform.service.langgraph.LangGraphAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AgentOrchestratorService {

    private final AgentSessionRepository sessionRepository;
    private final HistoryService historyService;
    private final ConsciousnessService consciousnessService;
    private final CliAgentService cliAgentService;
    private final CliAuditService cliAuditService;
    private final LangGraphAgentService langGraphAgentService;
    private final DifyAgentService difyAgentService;
    private final AgentProperties properties;
    private final SessionService sessionService;

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        return chat(request, null, null);
    }

    @Transactional
    public ChatResponse chat(ChatRequest request, Consumer<String> statusConsumer,
                             Consumer<String> streamConsumer) {
        AgentSession session = resolveSession(request);
        String userMessage = request.getMessage();
        sessionService.updateTitleFromFirstMessage(session, userMessage);
        String consciousnessContext = consciousnessService.buildContextPrompt(session.getId());

        historyService.saveMessage(session.getId(), MessageRole.USER, userMessage);

        SolutionType solution = resolveSolution(request, userMessage);
        emit(statusConsumer, "Routing to " + solution.name());

        String reply = dispatch(solution, session, userMessage, consciousnessContext, statusConsumer, streamConsumer);

        historyService.saveMessage(session.getId(), MessageRole.ASSISTANT, reply);
        consciousnessService.maybeRefreshSnapshot(session.getId());

        session.setLastActiveAt(Instant.now());
        sessionRepository.save(session);

        return ChatResponse.builder()
                .sessionId(session.getId())
                .externalSessionId(session.getExternalId())
                .reply(reply)
                .solutionUsed(solution.name())
                .consciousnessSummary(consciousnessService.latestSummary(session.getId()))
                .history(historyService.getHistory(session.getId()))
                .timestamp(Instant.now())
                .build();
    }

    private AgentSession resolveSession(ChatRequest request) {
        if (request.getSessionId() != null && !request.getSessionId().isBlank()) {
            return sessionRepository.findByExternalId(request.getSessionId())
                    .orElseGet(() -> createSession(request));
        }
        return createSession(request);
    }

    private AgentSession createSession(ChatRequest request) {
        String externalId = UUID.randomUUID().toString();
        CliProvider provider = request.getCliProvider() != null
                ? request.getCliProvider()
                : CliProvider.valueOf(properties.getCli().getDefaultProvider());

        AgentSession session = AgentSession.builder()
                .externalId(externalId)
                .title("Session " + externalId.substring(0, 8))
                .solutionType(request.getSolutionType() != null ? request.getSolutionType() : SolutionType.CLI_WRAPPER)
                .cliProvider(provider)
                .projectFolder(resolveProjectFolder(request))
                .status("ACTIVE")
                .build();

        return sessionRepository.save(session);
    }

    private String resolveProjectFolder(ChatRequest request) {
        if (request.getProjectFolder() != null && !request.getProjectFolder().isBlank()) {
            return request.getProjectFolder();
        }
        return properties.getWorkspaceRoot();
    }

    private SolutionType resolveSolution(ChatRequest request, String userMessage) {
        if (request.getSolutionType() != null) {
            return request.getSolutionType();
        }
        String lower = userMessage.toLowerCase();
        if (lower.contains("knowledge base") && properties.getDify().isEnabled()) {
            return SolutionType.DIFY;
        }
        if (lower.contains("multi-step") && properties.getLanggraph().isEnabled()) {
            return SolutionType.LANGGRAPH;
        }
        return SolutionType.CLI_WRAPPER;
    }

    private String dispatch(SolutionType solution, AgentSession session, String userMessage,
                            String consciousnessContext, Consumer<String> statusConsumer,
                            Consumer<String> streamConsumer) {
        return switch (solution) {
            case CLI_WRAPPER -> runCli(session, userMessage, statusConsumer, streamConsumer);
            case LANGGRAPH -> {
                emit(statusConsumer, "Invoking LangGraph sidecar...");
                String enriched = buildEnrichedPrompt(consciousnessContext, userMessage);
                yield langGraphAgentService.invoke(session.getExternalId(), enriched,
                        consciousnessService.latestSummary(session.getId()));
            }
            case DIFY -> {
                emit(statusConsumer, "Invoking Dify...");
                String enriched = buildEnrichedPrompt(consciousnessContext, userMessage);
                yield difyAgentService.invoke(session.getExternalId(), enriched);
            }
        };
    }

    private String runCli(AgentSession session, String userMessage,
                          Consumer<String> statusConsumer, Consumer<String> streamConsumer) {
        CliProvider provider = session.getCliProvider() != null
                ? session.getCliProvider()
                : CliProvider.CURSOR;

        emit(statusConsumer, "Running " + provider.name() + " CLI agent...");
        Path cwd = Path.of(session.getProjectFolder());
        CliExecutionResult result = cliAgentService.execute(
                provider, userMessage, session.getExternalId(), cwd, streamConsumer);
        cliAuditService.logExecution(session.getId(), result);
        return result.effectiveOutput();
    }

    private String buildEnrichedPrompt(String consciousnessContext, String userMessage) {
        if (consciousnessContext == null || consciousnessContext.isBlank()) {
            return userMessage;
        }
        return consciousnessContext + "\n\nUSER REQUEST:\n" + userMessage;
    }

    private void emit(Consumer<String> consumer, String message) {
        if (consumer != null) {
            consumer.accept(message);
        }
    }
}
