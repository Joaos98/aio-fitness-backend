package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.entity.WorkoutLog;
import com.joaosousa.aiofitness.service.WorkoutLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-logs")
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    public WorkoutLogController(WorkoutLogService workoutLogService) {
        this.workoutLogService = workoutLogService;
    }

    @GetMapping
    public List<WorkoutLog> findAll() {
        return workoutLogService.findAll();
    }

    @PostMapping
    public WorkoutLog save(@RequestBody WorkoutLog workoutLog) {
        return workoutLogService.save(workoutLog);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workoutLogService.delete(id);
    }
}