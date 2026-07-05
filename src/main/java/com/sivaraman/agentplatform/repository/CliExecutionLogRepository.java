/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.repository;

import com.sivaraman.agentplatform.entity.CliExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CliExecutionLogRepository extends JpaRepository<CliExecutionLog, UUID> {

    List<CliExecutionLog> findBySessionIdOrderByCreatedAtDesc(UUID sessionId);
}
