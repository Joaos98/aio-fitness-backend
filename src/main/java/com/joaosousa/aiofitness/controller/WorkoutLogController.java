package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.dto.HeatmapDayDto;
import com.joaosousa.aiofitness.dto.StreakDto;
import com.joaosousa.aiofitness.entity.WorkoutLog;
import com.joaosousa.aiofitness.service.WorkoutLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public Page<WorkoutLog> findAll(
            @PageableDefault(size = 20, sort = "logDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return workoutLogService.findAll(pageable);
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