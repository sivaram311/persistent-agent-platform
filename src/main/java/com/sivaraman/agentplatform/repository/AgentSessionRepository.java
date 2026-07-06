/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.repository;

import com.sivaraman.agentplatform.entity.AgentSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentSessionRepository extends JpaRepository<AgentSession, UUID> {

    Optional<AgentSession> findByExternalId(String externalId);

    List<AgentSession> findAllByOrderByLastActiveAtDesc(Pageable pageable);
}
