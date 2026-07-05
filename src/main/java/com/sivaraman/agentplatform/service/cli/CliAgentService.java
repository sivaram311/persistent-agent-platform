/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.service.cli;

import com.sivaraman.agentplatform.config.AgentProperties;
import com.sivaraman.agentplatform.domain.CliProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CliAgentService {

    private final AgentProperties properties;

    public CliExecutionResult execute(CliProvider provider, String prompt, String sessionId, Path workingDirectory) {
        List<String> command = wrapForWindows(buildCommand(provider, prompt, sessionId));
        long start = System.currentTimeMillis();

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workingDirectory.toFile());
        builder.redirectErrorStream(false);
        String path = System.getenv("PATH");
        if (path != null) {
            builder.environment().put("PATH", path);
        }

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        int exitCode = -1;

        try {
            Process process = builder.start();

            try (BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = outReader.readLine()) != null) {
                    stdout.append(line).append(System.lineSeparator());
                }
                while ((line = errReader.readLine()) != null) {
                    stderr.append(line).append(System.lineSeparator());
                }
            }

            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                stderr.append("CLI execution timed out after 10 minutes.");
            } else {
                exitCode = process.exitValue();
            }
        } catch (Exception ex) {
            log.error("CLI execution failed for provider {}", provider, ex);
            stderr.append(ex.getMessage());
        }

        return CliExecutionResult.builder()
                .provider(provider)
                .commandLine(String.join(" ", command))
                .exitCode(exitCode)
                .stdout(stdout.toString().trim())
                .stderr(stderr.toString().trim())
                .durationMs(System.currentTimeMillis() - start)
                .build();
    }

    private List<String> buildCommand(CliProvider provider, String prompt, String sessionId) {
        List<String> cmd = new ArrayList<>();
        AgentProperties.CliProperties cli = properties.getCli();

        switch (provider) {
            case CURSOR -> {
                cmd.add(cli.getCursorAgentPath());
                cmd.add("-p");
                cmd.add(prompt);
                cmd.add("--force");
                cmd.add("--trust");
                if (sessionId != null && !sessionId.isBlank()) {
                    cmd.add("--resume");
                    cmd.add(sessionId);
                }
            }
            case ANTIGRAVITY -> {
                cmd.add(cli.getAntigravityPath());
                cmd.add("-p");
                cmd.add(prompt);
                cmd.add("--force");
            }
            case GROK -> {
                cmd.add(cli.getGrokPath());
                cmd.add("-p");
                cmd.add(prompt);
                cmd.add("--output-format");
                cmd.add("streaming-json");
                cmd.add("--always-approve");
                if (sessionId != null && !sessionId.isBlank()) {
                    cmd.add("--session-id");
                    cmd.add(sessionId);
                }
            }
        }
        return cmd;
    }

    private List<String> wrapForWindows(List<String> command) {
        if (command.isEmpty()) {
            return command;
        }
        String executable = command.get(0);
        if (System.getProperty("os.name", "").toLowerCase().contains("win")
                && (executable.endsWith(".cmd") || executable.endsWith(".bat"))) {
            List<String> wrapped = new ArrayList<>();
            wrapped.add("cmd.exe");
            wrapped.add("/c");
            wrapped.addAll(command);
            return wrapped;
        }
        return command;
    }
}
