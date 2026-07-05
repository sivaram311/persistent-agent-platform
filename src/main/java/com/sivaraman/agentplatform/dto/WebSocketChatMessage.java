/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.dto;

import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.domain.SolutionType;
import lombok.Data;

@Data
public class WebSocketChatMessage {

    private String type;
    private String message;
    private String sessionId;
    private SolutionType solutionType;
    private CliProvider cliProvider;
    private String content;
    private String reply;
    private String externalSessionId;
    private String solutionUsed;
}
