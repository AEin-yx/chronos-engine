A Distributed System that reads, update, pause, play, start, repeat jobs at any given time called as a Job Scheduler.

| id | version |
| --- | --- |
|  |  |
|  |  |

To have a robust distributed job scheduler, a database must always have

—id helps us to use other services before commiting in the database

—version helps us in Optimistic Locking used in a read heavy scenerio.

<img width="791" height="392" alt="image" src="https://github.com/user-attachments/assets/6458e218-a85d-4a1e-b884-90a43cdece1e" />

Fig1 : Optimistic Locking in Action

---

### Identity of a Job

| name | description | owner_id |
| --- | --- | --- |
|  |  |  |
|  |  |  |

—A name must be non null and of limited length given to a Job

—Description is optional but it helps the user to remember Jobs

—owner_id prevents different users to influence each other jobs

---

### Status fields of Jobs

| status | enabled |
| --- | --- |
|  |  |
|  |  |
|  |  |

—status is a enum that stores`*SCHEDULED*, *RUNNING*, *COMPLETED*, *FAILED*, *CANCELLED*, *PAUSED` as string in the database. Users can configure this in the interface.*

—enabled is a way to pause our job if a third party service happens to be down or if user wants and configured from the interface itself 

---

### Job Type and Payload

This can be called as the Polymorphic Engine of the Scheduler. Its tells us what kind of Job we are handling( Send an API, Email) without the need to create table for each.

| type | payload | current_retry_count | JobRetryConfig |
| --- | --- | --- | --- |
|  |  |  |  |
|  |  |  |  |
|  |  |  |  |

— type can be `*HTTP*, *EMAIL*, *SQL` and depending on each type, we can execute different type of jobs.*

—payload is a very special column because the problem it solves are **:**

- An **HTTP Job** needs: `url`, `method`, `headers`, `body`.
- An **Email Job** needs: `recipient`, `subject`, `template_id`.
- A **SQL Job** needs: `query`, `connection_string`.

And we don’t need to create seperate columns in the database.

```sql
SELECT * FROM jobs WHERE payload->>'recipient' = 'boss@company.com’
```

payload stores it as a jsonb which is a parsed binary tree that speeds up execution of queries lightning fast.

—retryConfig also called as value object that stores info about jobs without any ID’s. They are a part of the job database itself because we use @Embedded and @Embeddable because joins decrease performance. 

| max_retires | retry_backoff_strategy | retry_delay_seconds |
| --- | --- | --- |
|  |  |  |
|  |  |  |

—max_retries helps us to ensure a task has maximum tries set by user before dropping the job.

—retry_backoff_strategy is `*NONE*, *FIXED*, *EXPONENTIAL` used if we want the job to execute with some strategy like fixed: try again and again and exponential:*  retry_delay_seconds=*delay * (2 ^ retryCount) with* Attempt 1: 60s Attempt 2: 120s Attempt 3: 240s .

—retry_delay_seconds is either every 1 min→ 60 sec or every 2 mins etc etc

---

### Scheduling

| schedule_type | run_at | cron_expression | timezone | next_run_at | last_run_at |
| --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |
|  |  |  |  |  |  |
|  |  |  |  |  |  |

—schedule_type is either one time Run this once at `runAt`, then stop or recurring Run this repeatedly based on `cronExpression`.
—run_at is checked only when schedule_type is one time. If it is null, the job runs immediately.

—cron_expression is a string expression that tells the scheduler which time should a job run at.

—timezone set the Geographical context that run_at etc store UTC time, timezone stores either ‘America/New_York’ or ‘Asia/Kolkata’

—next_run_at is the most important field of this section, it stores the future time at it should run and store it here everytime a job runs. The Polling thread doesn’t calculate cron expressions because its slow. The Poller can just query

```sql
SELECT * FROM jobs WHERE next_run_at <= NOW()
```

—last_run_at stores the history of the job’s execution time.

---

Priority

| priority |
| --- |
|  |
|  |
|  |

—priority sets the nature of job like `*LOW*, *NORMAL*, *HIGH*`

---

Worker Info

| last_worker_id | last_error_message |
| --- | --- |
|  |  |
|  |  |
|  |  |

—last worker id helps us to see  which worker performed the last operation helpful for cases when server crashes and holds a lock meanwhile its written in job but should have been in job execution table because creating a join would have wasted resources.

—last error message stores a message to help debug this complication during any failure

---

Statistics

| job_statistics |
| --- |
|  |
|  |
|  |
|  |

job statistic table has

| id | job | total_executions | success_count | failure_count | avg_execution_count_ms | last_execution_count_ms |
| --- | --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |  |
|  |  |  |  |  |  |  |

—the presence of lazy loading and cascade helps us to only load when required and cascade delete a table when the job table gets deleted

---

collection field

| executions |
| --- |
|  |
|  |
|  |

execution has the table

| id | run_id | job | status | attempt | started_at | finished_at | duration_ms | worker_id | error_message | extra | created_at |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |  |  |  |  |  |  |
|  |  |  |  |  |  |  |  |  |  |  |  |
|  |  |  |  |  |  |  |  |  |  |  |  |

—id used here is a long which helps in faster indexing and sort

—run id is used for distributed tracing. When a worker starts, it generates this UUID and passes to Logger.

—job has a job_id which is a foreign key 

—attempt gets updated to 1,2,.. when a retry happens.

—duration ms is also finished_at - started_at. We can do a query like "Show me all jobs that are taking longer than 5 seconds.”

—extra is a jsonb which allows us to query logs. This allows you to save **unstructured** execution data.

- *HTTP Job:* Save the Response Body ("404 Not Found").
- *SQL Job:* Save the "Rows Affected" count.
- *Email Job:* Save the "Message ID" from SendGrid.

—updatable = false in created at helps us to to prevent changing execution history. This means the created at will never be changed ever by JPA.

---

Audit

| metadata | created_at | updated_at | created_by | updated_by | deleted_at | deleted |
| --- | --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |  |
|  |  |  |  |  |  |  |
|  |  |  |  |  |  |  |

—**metadata** is a `jsonb` column that acts as a "sticky note" for administrative data. Unlike `payload` (which is for the Worker to execute), `metadata` is for the Manager/System to organize. You can query it to find "all jobs tagged with `{"department": "finance"}`" or `{"source": "dashboard"}` without adding new columns to the table.

**—created_at** uses `updatable = false` to permanently seal the birth timestamp of the job. This ensures that no matter how many times a user updates the job description or schedule, the original creation time remains tampered-proof for audit logs.

**—updated_at** is the "Last Modified" timestamp. It changes every single time the `Job` entity is saved. This is crucial for debugging; if a job starts behaving strangely, you check this field to see if someone modified the config 2 minutes ago.

**—created_by** is automatically filled by Spring Security (via `@CreatedBy`). It captures the `owner_id` or username of the person who originally defined the job. This creates a permanent paper trail: "Who set this up?" -> "User 123 did."
