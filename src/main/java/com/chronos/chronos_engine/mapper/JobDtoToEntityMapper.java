package com.chronos.chronos_engine.mapper;

import com.chronos.chronos_engine.dto.JobRequestDTO;
import com.chronos.chronos_engine.model.Job;

public class JobDtoToEntityMapper {
    public static Job toEntity(JobRequestDTO requestDTO) {
        Job job = new Job();
        job.setName(requestDTO.getName());
        job.setDescription(requestDTO.getDescription());
        job.setOwnerId(requestDTO.getOwnerId());
        job.setStatus(requestDTO.getStatus());
        job.setEnabled(requestDTO.isEnabled());
        job.setType(requestDTO.getType());
        job.setPayload(requestDTO.getPayload());
        job.setScheduleType(requestDTO.getScheduleType());
        job.setRunAt(requestDTO.getRunAt());
        job.setCronExpression(requestDTO.getCronExpression());
        job.setPriority(requestDTO.getPriority());
        return job;
    }
}
