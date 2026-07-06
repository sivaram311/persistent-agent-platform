/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.security;

import com.sivaraman.agentplatform.config.AgentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class WorkspaceGuardService {

    private final AgentProperties properties;

    public Path resolveSafePath(String requestedPath) {
        Path workspaceRoot = Path.of(properties.getWorkspaceRoot()).toAbsolutePath().normalize();
        Path target = requestedPath == null || requestedPath.isBlank()
                ? workspaceRoot
                : Path.of(requestedPath).toAbsolutePath().normalize();

        if (!target.startsWith(workspaceRoot)) {
            throw new SecurityException("Access denied: path must be within workspace root " + workspaceRoot);
        }
        return target;
    }
}
