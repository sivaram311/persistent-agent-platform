/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChatResponse {

    private UUID sessionId;
    private String externalSessionId;
    private String reply;
    private String solutionUsed;
    private String consciousnessSummary;
    private List<MessageDto> history;
    private Instant timestamp;
}
