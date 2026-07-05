/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.cli;

import com.sivaraman.agentplatform.domain.CliProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CliExecutionResult {

    private CliProvider provider;
    private String commandLine;
    private int exitCode;
    private String stdout;
    private String stderr;
    private long durationMs;

    public String effectiveOutput() {
        if (stdout != null && !stdout.isBlank()) {
            return stdout;
        }
        if (stderr != null && !stderr.isBlank()) {
            return stderr;
        }
        return "No output returned from CLI provider.";
    }
}
