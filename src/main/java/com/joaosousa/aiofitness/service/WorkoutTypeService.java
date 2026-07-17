package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.WorkoutType;
import com.joaosousa.aiofitness.repository.WorkoutTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutTypeService {
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutTypeService(WorkoutTypeRepository workoutTypeRepository) {
        this.workoutTypeRepository = workoutTypeRepository;
    }

    public WorkoutType save(WorkoutType workoutType) {
        return workoutTypeRepository.save(workoutType);
    }

    public List<WorkoutType> findAll() {
        return workoutTypeRepository.findAll();
    }
}
