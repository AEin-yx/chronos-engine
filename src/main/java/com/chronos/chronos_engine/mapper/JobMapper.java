package com.chronos.chronos_engine.mapper;

import com.chronos.chronos_engine.dto.JobResponseDTO;
import com.chronos.chronos_engine.model.Job;

public class JobMapper {
    public static JobResponseDTO toDTO(Job job){
        JobResponseDTO jobResponseDTO = new JobResponseDTO();
        jobResponseDTO.setId(job.getId());
        jobResponseDTO.setName(job.getName());
        jobResponseDTO.setDescription(job.getDescription());
        jobResponseDTO.setCronExpression(job.getCronExpression());
        jobResponseDTO.setTimeZone(job.getTimeZone());
        jobResponseDTO.setNextRunAt(job.getNextRunAt());
        jobResponseDTO.setScheduleType(job.getScheduleType());
        jobResponseDTO.setEnabled(job.isEnabled());
        jobResponseDTO.setStatus(job.getStatus());
        jobResponseDTO.setType(job.getType());
        jobResponseDTO.setPayload(job.getPayload());
        jobResponseDTO.setPriority(job.getPriority());
        jobResponseDTO.setMaxRetries(job.getRetryConfig().getMaxRetries());
        jobResponseDTO.setRetryDelaySeconds(job.getRetryConfig().getRetryDelaySeconds());
        jobResponseDTO.setRetryBackoffStrategy(job.getRetryConfig().getRetryBackoffStrategy());
        jobResponseDTO.setCurrentRetryCount(job.getCurrentRetryCount());
        jobResponseDTO.setCreatedAt(job.getCreatedAt());
        jobResponseDTO.setUpdatedAt(job.getUpdatedAt());
        jobResponseDTO.setOwnerId(job.getOwnerId());
        jobResponseDTO.setCreatedBy(job.getCreatedBy());
        return jobResponseDTO;
    }
}
