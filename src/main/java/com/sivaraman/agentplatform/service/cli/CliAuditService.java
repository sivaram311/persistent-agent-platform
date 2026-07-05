/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.cli;

import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.entity.CliExecutionLog;
import com.sivaraman.agentplatform.repository.CliExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CliAuditService {

    private static final int EXCERPT_MAX = 4000;

    private final CliExecutionLogRepository repository;

    @Transactional
    public void logExecution(UUID sessionId, CliExecutionResult result) {
        CliExecutionLog log = CliExecutionLog.builder()
                .sessionId(sessionId)
                .provider(result.getProvider())
                .commandLine(truncate(result.getCommandLine(), EXCERPT_MAX))
                .exitCode(result.getExitCode())
                .stdoutExcerpt(truncate(result.getStdout(), EXCERPT_MAX))
                .stderrExcerpt(truncate(result.getStderr(), EXCERPT_MAX))
                .durationMs(result.getDurationMs())
                .build();
        repository.save(log);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max) + "...";
    }
}
