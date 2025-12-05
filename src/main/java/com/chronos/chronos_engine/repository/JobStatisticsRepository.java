package com.chronos.chronos_engine.repository;

import com.chronos.chronos_engine.model.JobStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobStatisticsRepository extends JpaRepository<JobStatistics, UUID> {

}
