package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTypeRepository extends JpaRepository<WorkoutType, Long> {
}
