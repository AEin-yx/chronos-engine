package com.chronos.chronos_engine.repository;

import com.chronos.chronos_engine.model.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution,Long> {
}
