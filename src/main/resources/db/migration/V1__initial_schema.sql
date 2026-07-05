-- Persistent Agent Platform - Initial Schema
-- Author: SIVARAMAN R <sivaram311@gmail.com>

CREATE TABLE agent_sessions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id     VARCHAR(128) NOT NULL UNIQUE,
    title           VARCHAR(512),
    solution_type   VARCHAR(32)  NOT NULL DEFAULT 'CLI_WRAPPER',
    cli_provider    VARCHAR(32),
    project_folder  VARCHAR(1024),
    status          VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_active_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_external_id ON agent_sessions(external_id);
CREATE INDEX idx_sessions_last_active ON agent_sessions(last_active_at DESC);

CREATE TABLE conversation_messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID NOT NULL REFERENCES agent_sessions(id) ON DELETE CASCADE,
    role            VARCHAR(32) NOT NULL,
    content         TEXT NOT NULL,
    token_count     INTEGER,
    metadata_json   JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_session ON conversation_messages(session_id, created_at);

CREATE TABLE consciousness_snapshots (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID NOT NULL REFERENCES agent_sessions(id) ON DELETE CASCADE,
    summary         TEXT NOT NULL,
    key_facts       JSONB,
    preferences     JSONB,
    active_goals    JSONB,
    message_count   INTEGER NOT NULL DEFAULT 0,
    version         INTEGER NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_consciousness_session ON consciousness_snapshots(session_id, version DESC);

CREATE TABLE memory_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID REFERENCES agent_sessions(id) ON DELETE SET NULL,
    scope           VARCHAR(32) NOT NULL DEFAULT 'GLOBAL',
    category        VARCHAR(64),
    content         TEXT NOT NULL,
    embedding_ref   VARCHAR(256),
    importance      SMALLINT NOT NULL DEFAULT 5,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_memory_scope ON memory_entries(scope, category);
CREATE INDEX idx_memory_session ON memory_entries(session_id);

CREATE TABLE cli_execution_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID REFERENCES agent_sessions(id) ON DELETE SET NULL,
    provider        VARCHAR(32) NOT NULL,
    command_line    TEXT NOT NULL,
    exit_code       INTEGER,
    stdout_excerpt  TEXT,
    stderr_excerpt  TEXT,
    duration_ms     BIGINT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cli_logs_session ON cli_execution_logs(session_id, created_at DESC);

CREATE TABLE solution_routing_rules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name       VARCHAR(128) NOT NULL UNIQUE,
    solution_type   VARCHAR(32) NOT NULL,
    priority        INTEGER NOT NULL DEFAULT 100,
    condition_json  JSONB NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO solution_routing_rules (rule_name, solution_type, priority, condition_json) VALUES
('default-cli', 'CLI_WRAPPER', 100, '{"intent": "code_edit", "fallback": true}'),
('complex-orchestration', 'LANGGRAPH', 200, '{"intent": "multi_step", "min_steps": 3}'),
('visual-workflow', 'DIFY', 150, '{"intent": "rag_query", "requires_knowledge_base": true}');
