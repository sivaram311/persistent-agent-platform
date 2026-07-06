/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.dto;

import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.domain.SolutionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class SessionSummaryDto {

    private UUID id;
    private String externalId;
    private String title;
    private SolutionType solutionType;
    private CliProvider cliProvider;
    private String status;
    private long messageCount;
    private Instant createdAt;
    private Instant lastActiveAt;
}
