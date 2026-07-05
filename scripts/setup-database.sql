-- Database setup for Persistent Agent Platform
-- Author: SIVARAMAN R <sivaram311@gmail.com>
-- Run as postgres superuser

SELECT 'CREATE DATABASE agent_platform'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'agent_platform')\gexec

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'agent_user') THEN
        CREATE USER agent_user WITH ENCRYPTED PASSWORD 'AgentPlatform2026!';
    END IF;
END
$$;

ALTER DATABASE agent_platform OWNER TO agent_user;
GRANT ALL PRIVILEGES ON DATABASE agent_platform TO agent_user;

\c agent_platform

GRANT ALL ON SCHEMA public TO agent_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO agent_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO agent_user;
