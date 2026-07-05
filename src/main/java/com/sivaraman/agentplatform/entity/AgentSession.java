/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.entity;

import com.sivaraman.agentplatform.domain.CliProvider;
import com.sivaraman.agentplatform.domain.SolutionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "agent_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id", nullable = false, unique = true, length = 128)
    private String externalId;

    @Column(length = 512)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "solution_type", nullable = false, length = 32)
    private SolutionType solutionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "cli_provider", length = 32)
    private CliProvider cliProvider;

    @Column(name = "project_folder", length = 1024)
    private String projectFolder;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        lastActiveAt = now;
        if (status == null) {
            status = "ACTIVE";
        }
        if (solutionType == null) {
            solutionType = SolutionType.CLI_WRAPPER;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
