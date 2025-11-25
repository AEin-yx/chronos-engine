package com.chronos.chronos_engine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "job_statistics")
@Getter
@Setter
public class JobStatistics {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Share the same UUID as the Job table
    @JoinColumn(name = "id")
    private Job job;

    // Stats
    @Column(name = "total_executions")
    private int totalExecutions=0;

    @Column(name = "success_count")
    private int successCount =0;

    @Column(name = "failure_count")
    private int failureCount =0;

    @Column(name = "avg_execution_time_ms")
    private long avgExecutionTimeMs;

    @Column(name = "last_execution_time_ms")
    private long lastExecutionTimeMs ;
}
