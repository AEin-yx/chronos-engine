package com.chronos.chronos_engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JobStatisticsResponseDTO {
    private UUID id;

    // Stats
    private int totalExecutions=0;

    private int successCount =0;

    private int failureCount =0;

    private long avgExecutionTimeMs;

    private long lastExecutionTimeMs ;
}
