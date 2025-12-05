package com.chronos.chronos_engine.mapper;

import com.chronos.chronos_engine.dto.JobExecutionResponseDTO;
import com.chronos.chronos_engine.model.JobExecution;

public class JobExecMapper {
    public static JobExecutionResponseDTO toDTO(JobExecution jobExecution) {
        JobExecutionResponseDTO jobExecutionResponseDTO = new JobExecutionResponseDTO();
        jobExecutionResponseDTO.setId(jobExecution.getId());
        jobExecutionResponseDTO.setRunId(jobExecution.getRunId());
        jobExecutionResponseDTO.setStatus(jobExecution.getStatus());
        jobExecutionResponseDTO.setAttempt(jobExecution.getAttempt());
        jobExecutionResponseDTO.setStartedAt(jobExecution.getStartedAt());
        jobExecutionResponseDTO.setFinishedAt(jobExecution.getFinishedAt());
        jobExecutionResponseDTO.setDurationMs(jobExecution.getDurationMs());
        jobExecutionResponseDTO.setWorkerId(jobExecution.getWorkerId());
        jobExecutionResponseDTO.setErrorMessage(jobExecution.getErrorMessage());
        jobExecutionResponseDTO.setExtra(jobExecution.getExtra());
        jobExecutionResponseDTO.setCreatedAt(jobExecution.getCreatedAt());
        return jobExecutionResponseDTO;
    }
}
