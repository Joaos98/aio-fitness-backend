package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
