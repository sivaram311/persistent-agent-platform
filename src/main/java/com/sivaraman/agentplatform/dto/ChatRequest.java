/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.dto;

import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.domain.SolutionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank
    private String message;

    private String sessionId;

    private SolutionType solutionType;

    private CliProvider cliProvider;

    private String projectFolder;
}
