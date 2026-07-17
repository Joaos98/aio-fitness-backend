package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
}
