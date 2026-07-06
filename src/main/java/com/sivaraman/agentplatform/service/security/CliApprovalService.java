/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.security;

import com.sivaraman.agentplatform.config.AgentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CliApprovalService {

    private static final List<Pattern> BLOCKED_PATTERNS = List.of(
            Pattern.compile("(?i)\\brm\\s+-rf\\b"),
            Pattern.compile("(?i)\\bdel\\s+/[fq]"),
            Pattern.compile("(?i)\\bformat\\s+[a-z]:"),
            Pattern.compile("(?i)\\bshutdown\\b"),
            Pattern.compile("(?i)\\brestart-computer\\b"),
            Pattern.compile("(?i)\\bdrop\\s+database\\b"),
            Pattern.compile("(?i)\\btruncate\\s+table\\b")
    );

    public void validatePrompt(String prompt) {
        if (prompt == null) {
            return;
        }
        for (Pattern pattern : BLOCKED_PATTERNS) {
            if (pattern.matcher(prompt).find()) {
                throw new SecurityException(
                        "Blocked: prompt contains potentially destructive command. Review required.");
            }
        }
    }
}
