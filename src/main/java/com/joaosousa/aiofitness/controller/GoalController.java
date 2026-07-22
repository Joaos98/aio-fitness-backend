package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.dto.GoalProgressDto;
import com.joaosousa.aiofitness.entity.Goal;
import com.joaosousa.aiofitness.entity.GoalStatus;
import com.joaosousa.aiofitness.service.GoalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public List<GoalProgressDto> findAll() {
        return goalService.findAllWithProgress();
    }

    @PostMapping
    public Goal save(@RequestBody Goal goal) {
        return goalService.save(goal);
    }

    @PatchMapping("/{id}/status")
    public Goal updateStatus(@PathVariable Long id, @RequestParam GoalStatus status) {
        return goalService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
    }
}