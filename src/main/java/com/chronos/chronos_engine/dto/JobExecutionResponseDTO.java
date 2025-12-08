package com.chronos.chronos_engine.dto;

import com.chronos.chronos_engine.model.Status;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
public class JobExecutionResponseDTO {

    private Long id;

    private UUID runId;

    private Status status; // Use the existing Status enum

    private int attempt;

    private ZonedDateTime startedAt;

    private ZonedDateTime finishedAt;

    private Long durationMs;

    private String workerId;

    private String errorMessage;

    // Execution-specific data/logs
    private JsonNode extra;

    private ZonedDateTime createdAt;
}
