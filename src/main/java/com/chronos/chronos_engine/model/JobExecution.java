package com.chronos.chronos_engine.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_executions")
@Getter @Setter
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Maps to BIGSERIAL
    private Long id;

    @Column(name = "run_id", nullable = false, unique = true, updatable = false)
    private UUID runId = UUID.randomUUID();

    // Many executions belong to one Job (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status; // Use the existing Status enum

    @Column(nullable = false)
    private int attempt;

    @Column(name = "started_at", nullable = false)
    private ZonedDateTime startedAt;

    @Column(name = "finished_at")
    private ZonedDateTime finishedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "worker_id", length = 100)
    private String workerId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Execution-specific data/logs
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode extra;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}