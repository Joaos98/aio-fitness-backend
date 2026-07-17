package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.Goal;
import com.joaosousa.aiofitness.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal save(Goal goal) {
        return goalRepository.save(goal);
    }

    public List<Goal> findAll() {
        return goalRepository.findAll();
    }
}
