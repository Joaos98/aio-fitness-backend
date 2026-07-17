package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.BodyMetrics;
import com.joaosousa.aiofitness.repository.BodyMetricsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodyMetricsService {
    private final BodyMetricsRepository bodyMetricsRepository;

    public BodyMetricsService(BodyMetricsRepository bodyMetricsRepository) {
        this.bodyMetricsRepository = bodyMetricsRepository;
    }

    public BodyMetrics save(BodyMetrics bodyMetrics) {
        return bodyMetricsRepository.save(bodyMetrics);
    }

    public List<BodyMetrics> findAll() {
        return bodyMetricsRepository.findAll();
    }

    public void delete(Long id) {
        bodyMetricsRepository.deleteById(id);
    }
}
