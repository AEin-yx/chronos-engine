package com.chronos.chronos_engine.service;

import com.chronos.chronos_engine.dto.*;
import com.chronos.chronos_engine.exception.JobNotFoundException;
import com.chronos.chronos_engine.exception.ResourceNotFoundException;
import com.chronos.chronos_engine.mapper.JobDtoToEntityMapper;
import com.chronos.chronos_engine.mapper.JobExecMapper;
import com.chronos.chronos_engine.mapper.JobMapper;
import com.chronos.chronos_engine.mapper.JobStatMapper;
import com.chronos.chronos_engine.model.Job;
import com.chronos.chronos_engine.model.JobExecution;
import com.chronos.chronos_engine.model.Schedule;
import com.chronos.chronos_engine.repository.JobExecutionRepository;
import com.chronos.chronos_engine.repository.JobRepository;
import com.chronos.chronos_engine.repository.JobStatisticsRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
import java.util.UUID;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final JobStatisticsRepository jobStatisticsRepository;
    private final JobExecutionRepository jobExecutionRepository;

    public JobService(JobRepository jobRepository, JobStatisticsRepository jobStatisticsRepository, JobExecutionRepository jobExecutionRepository) {
        this.jobRepository = jobRepository;
        this.jobStatisticsRepository = jobStatisticsRepository;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    public Page<JobResponseDTO> getJobs(Pageable pageable) {
        Page<Job> jobs = jobRepository.findAll(pageable);
        return jobs.map(JobMapper::toDTO);
    }

    public JobResponseDTO getJobById(UUID id) {
        return jobRepository.findById(id).map(JobMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    public JobResponseDTO createJob(JobRequestDTO jobRequestDTO) {
        Job entity = JobDtoToEntityMapper.toEntity(jobRequestDTO);
        if(entity.getScheduleType().equals(Schedule.ONETIME)){
            entity.setCronExpression(null);
        }
        if(entity.getScheduleType().equals(Schedule.RECURRING) && entity.getCronExpression()==null){
            entity.setCronExpression("*****");
        }
        Job save = jobRepository.save(entity);
        return JobMapper.toDTO(save);
    }

    public String deleteJob(UUID id){
        return jobRepository.findById(id)
                .map(job -> {
                jobRepository.deleteById(id);
                return "Deleted Job with id: "+id;
            }).orElse("No Such Job exists for id: \" + id + \". Please check the ID again");
    }

    @Transactional
    public JobResponseDTO updateJob(UUID id, UpdateJobRequestDTO updateReq) {
        Job job=jobRepository.findById(id).orElseThrow(()-> new JobNotFoundException(id));

        // Check if any process updated the job in the meantime
        if(updateReq.getVersion()!=null && !updateReq.getVersion().equals(job.getVersion()) ){
            throw new OptimisticLockException("Job was modified by another process. "+
                    "current version: "+job.getVersion()+
                    "expected Version: "+updateReq.getVersion());
        }

        applyUpdate(job, updateReq);

        try {
            Job saved = jobRepository.save(job); // will throw OptimisticLockingFailureException if someone else changed it
            return JobMapper.toDTO(saved);
        } catch (OptimisticLockingFailureException ex) {
            // Convert to a domain-level exception or ResponseStatusException
            throw new ConcurrentModificationException("Job was modified concurrently. Please refresh and retry.");
        }
    }

    private void applyUpdate(Job job, UpdateJobRequestDTO requestDTO){
        if(requestDTO.getName()!=null){
            job.setName(requestDTO.getName());
        }
        if(requestDTO.getDescription()!=null){
            job.setDescription(requestDTO.getDescription());
        }
        if(requestDTO.getOwnerId()!=null){
            job.setOwnerId(requestDTO.getOwnerId());
        }
        if(requestDTO.getStatus()!=null){
            job.setStatus(requestDTO.getStatus());
        }
        if(requestDTO.getType()!=null){
            job.setType(requestDTO.getType());
        }
        if(requestDTO.getPayload()!=null){
            job.setPayload(requestDTO.getPayload());
        }
        if(requestDTO.getScheduleType()!=null){
            job.setScheduleType(requestDTO.getScheduleType());
        }
        if(requestDTO.getRunAt()!=null){
            job.setRunAt(requestDTO.getRunAt());
        }
        if(requestDTO.getCronExpression()!=null){
            job.setCronExpression(requestDTO.getCronExpression());
        }
        if(requestDTO.getPriority()!=null){
            job.setPriority(requestDTO.getPriority());
        }

        if (requestDTO.getScheduleType() == Schedule.ONETIME) {
            job.setCronExpression(null);
        }

        if(requestDTO.getScheduleType() == Schedule.RECURRING){
            job.setCronExpression("*****");
        }
    }

    // Statistics
    public JobStatisticsResponseDTO jobStats(UUID id) {
        return jobStatisticsRepository.findById(id).map(JobStatMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException("Job statistics not found"));
    }

    public Page<JobStatisticsResponseDTO> jobAllStats(Pageable pageable) {
        return jobStatisticsRepository.findAll(pageable).map(JobStatMapper::toDTO);
    }

    // Execution history
    public JobExecutionResponseDTO jobExec(Long id) {
        return jobExecutionRepository.findById(id).map(JobExecMapper::toDTO).orElseThrow(()->
            new ResourceNotFoundException("Job Execution History not found"));
    }

    //All Execution history
    public Page<JobExecutionResponseDTO> jobAllExex(Pageable pageable) {
        Page<JobExecution> executions = jobExecutionRepository.findAll(pageable);
        return executions.map(JobExecMapper::toDTO);
    }
}
