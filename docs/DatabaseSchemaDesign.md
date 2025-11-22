# Database Design Schema

![image.png](/static/image.png "optimisticlocking")

## Core Identity

| id | version |
|---|---------|
|   |         |
|   |         |

id is used for cross‑service correlation before committing to the database.  
version supports optimistic locking in read‑heavy scenarios.

---

## Identity of a Job

| name | description | owner_id |
|------|-------------|----------|
|      |             |          |
|      |             |          |

name is required and short.  
description is optional but helpful.  
owner_id isolates user‑owned jobs.

---

## Job Status

| status | enabled |
|--------|---------|
|        |         |
|        |         |

status stores: `SCHEDULED`, `RUNNING`, `COMPLETED`, `FAILED`, `CANCELLED`, `PAUSED`.  
enabled lets the user pause a job.

---

## Job Type & Payload

| type | payload | current_retry_count | retry_config |
|------|---------|----------------------|---------------|
|      |         |                      |               |
|      |         |                      |               |

### Payload rules

- **HTTP Job:** `url`, `method`, `headers`, `body`
- **Email Job:** `recipient`, `subject`, `template_id`
- **SQL Job:** `query`, `connection_string`

payload is stored as `jsonb` for fast querying.

### Retry Config

| max_retries | backoff_strategy | retry_delay_seconds |
|-------------|------------------|----------------------|
|             |                  |                      |
|             |                  |                      |

backoff strategies: `NONE`, `FIXED`, `EXPONENTIAL`.

---

## Scheduling

| schedule_type | run_at | cron_expression | timezone | next_run_at | last_run_at |
|----------------|--------|------------------|----------|--------------|--------------|
|                |        |                  |          |              |              |
|                |        |                  |          |              |              |

run_at is used only for one‑time jobs.  
next_run_at is precomputed to avoid cron parsing during polling.

---

## Priority

| priority |
|----------|
|          |
|          |

Values: `LOW`, `NORMAL`, `HIGH`.

---

## Worker Info

| last_worker_id | last_error_message |
|----------------|--------------------|
|                |                    |
|                |                    |

---

## Statistics

| job_statistics |
|----------------|
|                |
|                |
|                |

### Job Statistics Table

| id | job | total_executions | success_count | failure_count | avg_execution_ms | last_execution_ms |
|----|-----|------------------|----------------|----------------|-------------------|---------------------|
|    |     |                  |                |                |                   |                     |
|    |     |                  |                |                |                   |                     |

---

## Executions

| executions |
|-----------|
|           |
|           |

### Execution Table

| id | run_id | job | status | attempt | started_at | finished_at | duration_ms | worker_id | error_message | extra | created_at |
|----|---------|-----|--------|---------|-------------|--------------|--------------|-----------|----------------|--------|-------------|
|    |         |     |        |         |             |              |              |           |                |        |             |
|    |         |     |        |         |             |              |              |           |                |        |             |
|    |         |     |        |         |             |              |              |           |                |        |             |

extra is jsonb for unstructured execution logs.

---

## Audit

| metadata | created_at | updated_at | created_by | updated_by | deleted_at | deleted |
|----------|-------------|-------------|-------------|-------------|-------------|---------|
|          |             |             |             |             |             |         |
|          |             |             |             |             |             |         |

metadata is jsonb for admin‑only information.  
created_at is immutable; updated_at changes on every update.

