package com.chronos.chronos_engine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import tools.jackson.databind.JsonNode;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs", indexes = {
        // CRITICAL: Composite index for the Polling Thread
        @Index(name = "idx_job_poll", columnList = "status, next_run_at, priority"),
        // Index for searching by owner
        @Index(name = "idx_job_owner", columnList = "owner_id")
})
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Optimistic locking to avoid concurrent job pickups
    @Version
    private Long version;

    // Identity
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    //Status fields
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status=Status.SCHEDULED;

    private boolean enabled=true;

    //Job type and payload
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Type type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    // to save from accidental resets
    @Column(name = "current_retry_count")
    private int currentRetryCount = 0;

    @Embedded
    private JobRetryConfig retryConfig = new JobRetryConfig();

    // Scheduling
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private Schedule scheduleType;

    @Column(name = "run_at")
    private ZonedDateTime runAt;

    @Column(name = "cron_expression")
    private String cronExpression;

    private String timeZone="UTC";

    @Column(name = "next_run_at")
    private ZonedDateTime nextRunAt;

    @Column(name = "last_run_at")
    private ZonedDateTime lastRunAt;

    // Priority
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Priority priority=Priority.NORMAL;

    // Worker info
    @Column(name = "last_worker_id")
    private String lastWorkerId;
    private String lastErrorMessage;

    // Link to stats
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private JobStatistics jobStatistics;

    // collection field
    private List<JobExecution> executions = new ArrayList<>();

    // Audit
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode metadata;


    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt ;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt ;

    @CreatedBy
    private String createdBy ;

    @LastModifiedBy
    private String updatedBy ;


    // soft delete support
    private ZonedDateTime deletedAt;
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
        if (this.jobStatistics == null) {
            this.jobStatistics = new JobStatistics();
            this.jobStatistics.setJob(this);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}
