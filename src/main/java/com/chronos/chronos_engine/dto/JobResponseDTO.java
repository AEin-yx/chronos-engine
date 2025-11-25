package com.chronos.chronos_engine.dto;

import com.chronos.chronos_engine.model.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class JobResponseDTO {
    // Core Identity
    private UUID id;
    private String name;
    private String description;

    // Scheduling
    private String cronExpression;
    private String timeZone;
    private ZonedDateTime nextRunAt;
    private Schedule scheduleType;

    // Status & Control
    private boolean enabled;
    private Status status;

    // Execution
    private Type type;
    private JsonNode payload;
    private Priority priority;

    // Retry Logic (Flattened from JobRetryConfig)
    private int maxRetries;
    private int retryDelaySeconds;
    private Retrybackoff retryBackoffStrategy;
    private int currentRetryCount; // Added this so you can see if it's failing

    // Metadata
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String ownerId;
    private String createdBy;
}
