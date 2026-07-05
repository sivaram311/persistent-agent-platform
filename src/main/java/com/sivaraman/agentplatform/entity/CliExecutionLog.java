/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.entity;

import com.sivaraman.agentplatform.domain.CliProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cli_execution_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CliExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id")
    private UUID sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CliProvider provider;

    @Column(name = "command_line", nullable = false, columnDefinition = "TEXT")
    private String commandLine;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "stdout_excerpt", columnDefinition = "TEXT")
    private String stdoutExcerpt;

    @Column(name = "stderr_excerpt", columnDefinition = "TEXT")
    private String stderrExcerpt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
