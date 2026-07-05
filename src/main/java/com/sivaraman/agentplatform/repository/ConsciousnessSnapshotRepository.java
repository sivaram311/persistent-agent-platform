/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.repository;

import com.sivaraman.agentplatform.entity.ConsciousnessSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsciousnessSnapshotRepository extends JpaRepository<ConsciousnessSnapshot, UUID> {

    Optional<ConsciousnessSnapshot> findFirstBySessionIdOrderByVersionDesc(UUID sessionId);
}
