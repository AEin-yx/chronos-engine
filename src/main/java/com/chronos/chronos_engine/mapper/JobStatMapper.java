package com.chronos.chronos_engine.mapper;

import com.chronos.chronos_engine.dto.JobStatisticsResponseDTO;
import com.chronos.chronos_engine.model.JobStatistics;

public class JobStatMapper {
    public static JobStatisticsResponseDTO toDTO(JobStatistics jobStatistics) {
        JobStatisticsResponseDTO jobStatisticsResponseDTO = new JobStatisticsResponseDTO();
        jobStatisticsResponseDTO.setId(jobStatistics.getId());
        jobStatisticsResponseDTO.setTotalExecutions(jobStatistics.getTotalExecutions());
        jobStatisticsResponseDTO.setSuccessCount(jobStatisticsResponseDTO.getSuccessCount());
        jobStatisticsResponseDTO.setFailureCount(jobStatisticsResponseDTO.getFailureCount());
        jobStatisticsResponseDTO.setAvgExecutionTimeMs(jobStatisticsResponseDTO.getAvgExecutionTimeMs());
        jobStatisticsResponseDTO.setLastExecutionTimeMs(jobStatisticsResponseDTO.getLastExecutionTimeMs());
        return jobStatisticsResponseDTO;
    }
}
