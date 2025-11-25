package com.chronos.chronos_engine.controller;

import com.chronos.chronos_engine.dto.JobRequestDTO;
import com.chronos.chronos_engine.dto.JobResponseDTO;
import com.chronos.chronos_engine.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDTO>> getJobs() {
        List<JobResponseDTO> jobs = jobService.getJobs();
        return ResponseEntity.ok().body(jobs);
    }
    // jobs according to id
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<List<JobResponseDTO>> getJobsById(@PathVariable("jobId") UUID id) {
        List<JobResponseDTO> job= jobService.getJobsById(id);
        return ResponseEntity.ok().body(job);
    }
    // create a new job
    @PostMapping("/jobs")
    public ResponseEntity<JobResponseDTO> createJob(@RequestBody JobRequestDTO jobRequest) {
        JobResponseDTO jobResponse = jobService.createJob(jobRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponse);
    }
}
