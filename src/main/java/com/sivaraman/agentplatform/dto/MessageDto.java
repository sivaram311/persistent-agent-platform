/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.dto;

import com.sivaraman.agentplatform.domain.MessageRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MessageDto {

    private UUID id;
    private MessageRole role;
    private String content;
    private Instant createdAt;
}
