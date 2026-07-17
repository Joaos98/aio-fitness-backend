package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.WorkoutLog;
import com.joaosousa.aiofitness.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutLogService {
    private final WorkoutLogRepository workoutLogRepository;

    public WorkoutLogService(WorkoutLogRepository workoutLogRepository) {
        this.workoutLogRepository = workoutLogRepository;
    }

    public WorkoutLog save(WorkoutLog workoutLog) {
        return workoutLogRepository.save(workoutLog);
    }

    public List<WorkoutLog> findAll() {
        return workoutLogRepository.findAll();
    }

    public void delete(Long id) {
        workoutLogRepository.deleteById(id);
    }
}
