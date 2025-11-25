-- 1. Clean slate
TRUNCATE TABLE job_statistics CASCADE;
TRUNCATE TABLE job_executions CASCADE;
TRUNCATE TABLE jobs CASCADE;

-- 2. Insert Job 1: Google Health Check
INSERT INTO jobs (
    id,
    version,
    name,
    description,
    owner_id,
    status,
    enabled,
    deleted,  -- <--- ADDED COLUMN
    type,
    payload,
    -- RETRY CONFIG
    max_retries,
    retry_backoff_strategy,
    retry_delay_seconds,
    current_retry_count,
    -- SCHEDULE
    schedule_type,
    cron_expression,
    time_zone,
    next_run_at,
    priority,
    -- AUDIT
    created_at,
    updated_at,
    metadata
) VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
    0,
    'Google Health Check',
    'Pings Google homepage every minute',
    'admin_user',
    'SCHEDULED',
    true,
    false,    -- <--- ADDED VALUE (Not Deleted)
    'HTTP',
    '{"url": "https://google.com", "method": "GET"}'::jsonb,
    3,
    'EXPONENTIAL',
    60,
    0,
    'RECURRING',
    '0 * * * * ?',
    'UTC',
    NOW(),
    'HIGH',
    NOW(),
    NOW(),
    '{"department": "DevOps"}'::jsonb
);

-- 3. Insert Stats for Job 1
INSERT INTO job_statistics (id, total_executions, success_count, failure_count, avg_execution_time_ms, last_execution_time_ms)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid, 0, 0, 0, 0, 0);

-- 4. Insert Job 2: DB Cleanup
INSERT INTO jobs (
    id, version, name, description, owner_id, status, enabled, deleted, -- <--- ADDED COLUMN
    type, payload,
    max_retries, retry_backoff_strategy, retry_delay_seconds, current_retry_count,
    schedule_type, run_at, next_run_at, priority, created_at, updated_at, metadata
) VALUES (
    'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b22'::uuid,
    0,
    'DB Cleanup',
    'Removes old logs',
    'db_admin',
    'SCHEDULED',
    true,
    false,   -- <--- ADDED VALUE
    'SQL',
    '{"query": "DELETE FROM logs"}'::jsonb,
    5, 'FIXED', 300, 0,
    'ONETIME',
    NOW() + INTERVAL '1 hour',
    NOW() + INTERVAL '1 hour',
    'LOW',
    NOW(), NOW(),
    NULL
);

-- 5. Insert Stats for Job 2
INSERT INTO job_statistics (id, total_executions, success_count, failure_count, avg_execution_time_ms, last_execution_time_ms)
VALUES ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380b22'::uuid, 0, 0, 0, 0, 0);