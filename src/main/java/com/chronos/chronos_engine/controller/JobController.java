package com.chronos.chronos_engine.controller;

import com.chronos.chronos_engine.dto.*;
import com.chronos.chronos_engine.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Show all Jobs
    @GetMapping("/jobs")
    public ResponseEntity<Page<JobResponseDTO>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.ASC,"nextRunAt"));
        Page<JobResponseDTO> jobs = jobService.getJobs(pageable);
        return ResponseEntity.ok(jobs);
    }

    // jobs according to id
    @GetMapping("/job/{jobId}")
    public ResponseEntity<JobResponseDTO> getJobsById(@PathVariable("jobId") UUID id) {
        JobResponseDTO job= jobService.getJobById(id);
        return ResponseEntity.ok().body(job);
    }

    // create a new job
    @PostMapping("/job/create")
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO jobRequest) {
        JobResponseDTO jobResponse = jobService.createJob(jobRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponse);
    }

    // delete a job by id
    @DeleteMapping("/job/delete/{id}")
    public String deleteJob(@PathVariable("id") UUID id){
        return jobService.deleteJob(id);
    }

    //Update a job by id
    @PatchMapping("/job/update/{id}")
    public ResponseEntity<JobResponseDTO> updateJob(@PathVariable("id")UUID id, @RequestBody UpdateJobRequestDTO updateJobRequestDTO) {
        JobResponseDTO jobResponse = jobService.updateJob(id,updateJobRequestDTO);
        return ResponseEntity.ok(jobResponse);
    }

    //See all executions of a Job
    @GetMapping("/jobs/executions")
    public ResponseEntity<Page<JobExecutionResponseDTO>> jobAllExec(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startedAt,desc") String sort
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<JobExecutionResponseDTO> jobExecutionList = jobService.jobAllExex(pageable);
        return ResponseEntity.ok(jobExecutionList);
    }
    //See past executions of a Job with id
    @GetMapping("/job/execution/{id}")
    public ResponseEntity<JobExecutionResponseDTO> jobExec(@PathVariable("id") Long id) {
        JobExecutionResponseDTO jobExecution = jobService.jobExec(id);
        return ResponseEntity.ok(jobExecution);
    }

    //See the statistic of all Jobs
    @GetMapping("/jobs/statistics")
    public ResponseEntity<Page<JobStatisticsResponseDTO>> jobAllStats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<JobStatisticsResponseDTO> jobStatisticsList = jobService.jobAllStats(pageable);
        return ResponseEntity.ok(jobStatisticsList);
    }

    //See the statistics of a Job with id
    @GetMapping("/job/statistic/{id}")
    public ResponseEntity<JobStatisticsResponseDTO> jobStats(@PathVariable("id") UUID id) {
        JobStatisticsResponseDTO jobStats = jobService.jobStats(id);
        return ResponseEntity.ok(jobStats);
    }
}
