package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.entity.BodyMetrics;
import com.joaosousa.aiofitness.service.BodyMetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/body-metrics")
public class BodyMetricsController {

    private final BodyMetricsService bodyMetricsService;

    public BodyMetricsController(BodyMetricsService bodyMetricsService) {
        this.bodyMetricsService = bodyMetricsService;
    }

    @GetMapping
    public List<BodyMetrics> findAll() {
        return bodyMetricsService.findAll();
    }

    @PostMapping
    public BodyMetrics save(@RequestBody BodyMetrics bodyMetrics) {
        return bodyMetricsService.save(bodyMetrics);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bodyMetricsService.delete(id);
    }

    @PutMapping("/{id}")
    public BodyMetrics updateBodyMetrics(@PathVariable Long id, @RequestBody BodyMetrics updated) {
        return bodyMetricsService.updateBodyMetrics(id, updated);
    }
}