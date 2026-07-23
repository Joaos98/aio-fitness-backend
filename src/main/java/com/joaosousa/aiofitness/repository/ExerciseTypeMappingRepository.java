package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.ExerciseTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTypeMappingRepository extends JpaRepository<ExerciseTypeMapping, Integer> {
}
