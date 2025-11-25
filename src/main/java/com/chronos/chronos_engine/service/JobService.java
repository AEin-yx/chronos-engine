package com.chronos.chronos_engine.service;

import com.chronos.chronos_engine.dto.JobRequestDTO;
import com.chronos.chronos_engine.dto.JobResponseDTO;
import com.chronos.chronos_engine.mapper.JobDtoToEntityMapper;
import com.chronos.chronos_engine.mapper.JobMapper;
import com.chronos.chronos_engine.model.Job;
import com.chronos.chronos_engine.model.Schedule;
import com.chronos.chronos_engine.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobService {
    private JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<JobResponseDTO> getJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(JobMapper::toDTO).collect(Collectors.toList());
    }

    public List<JobResponseDTO> getJobsById( UUID id) {
        Optional<Job> jobs = jobRepository.findById(id);
        return jobs.stream().map(JobMapper::toDTO).collect(Collectors.toList());
    }

    public JobResponseDTO createJob(JobRequestDTO jobRequestDTO) {
        Job entity = JobDtoToEntityMapper.toEntity(jobRequestDTO);
        if(entity.getScheduleType().equals(Schedule.ONETIME)){
            entity.setCronExpression(null);
        }
        Job save = jobRepository.save(entity);
        return JobMapper.toDTO(save);
    }
}
