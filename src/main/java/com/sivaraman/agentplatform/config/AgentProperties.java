/*
 * Persistent Agent Platform
 * Copyright (c) 2026 SIVARAMAN R <sivaram311@gmail.com>
 */
package com.sivaraman.agentplatform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "agent")
@Getter
@Setter
public class AgentProperties {

    private String workspaceRoot = "E:/MyWorkspace";
    private CliProperties cli = new CliProperties();
    private ConsciousnessProperties consciousness = new ConsciousnessProperties();
    private LangGraphProperties langgraph = new LangGraphProperties();
    private DifyProperties dify = new DifyProperties();

    @Getter
    @Setter
    public static class CliProperties {
        private String cursorAgentPath = "cursor-agent";
        private String antigravityPath = "agy";
        private String grokPath = "grok";
        private String defaultProvider = "CURSOR";
        private int sessionTimeoutMinutes = 120;
    }

    @Getter
    @Setter
    public static class ConsciousnessProperties {
        private int maxContextMessages = 50;
        private int summaryThresholdMessages = 30;
        private int embeddingDimension = 1536;
    }

    @Getter
    @Setter
    public static class LangGraphProperties {
        private boolean enabled;
        private String serviceUrl = "http://localhost:8090";
    }

    @Getter
    @Setter
    public static class DifyProperties {
        private boolean enabled;
        private String baseUrl = "http://localhost:3000";
        private String apiKey;
    }
}
