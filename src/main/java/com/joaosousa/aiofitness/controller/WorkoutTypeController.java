package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.entity.WorkoutType;
import com.joaosousa.aiofitness.service.WorkoutTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-types")
public class WorkoutTypeController {

    private final WorkoutTypeService workoutTypeService;

    public WorkoutTypeController(WorkoutTypeService workoutTypeService) {
        this.workoutTypeService = workoutTypeService;
    }

    @GetMapping
    public List<WorkoutType> findAll() {
        return workoutTypeService.findAll();
    }

    @PostMapping
    public WorkoutType save(@RequestBody WorkoutType workoutType) {
        return workoutTypeService.save(workoutType);
    }
}