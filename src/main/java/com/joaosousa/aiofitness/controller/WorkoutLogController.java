package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.dto.HeatmapDayDto;
import com.joaosousa.aiofitness.dto.StreakDto;
import com.joaosousa.aiofitness.entity.WorkoutLog;
import com.joaosousa.aiofitness.service.WorkoutLogService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PutMapping("/{id}")
    public WorkoutLog updateWorkoutLog(@PathVariable Long id, @RequestBody WorkoutLog updated) {
        return workoutLogService.updateWorkoutLog(id, updated);
    }

    @GetMapping("/heatmap")
    public List<HeatmapDayDto> getHeatmap(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return workoutLogService.getHeatmapData(startDate, endDate);
    }

    @GetMapping("/streaks")
    public StreakDto getStreaks() {
        return workoutLogService.calculateStreaks();
    }
}