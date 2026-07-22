package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.BodyMetrics;
import com.joaosousa.aiofitness.repository.BodyMetricsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BodyMetricsService {
    private final BodyMetricsRepository bodyMetricsRepository;
    private final InsightService insightService;

    public BodyMetricsService(BodyMetricsRepository bodyMetricsRepository, InsightService insightService) {
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.insightService = insightService;
    }

    public BodyMetrics save(BodyMetrics bodyMetrics) {
        BodyMetrics saved = bodyMetricsRepository.save(bodyMetrics);

        InsightService.InsightResult result = insightService.generateInsight(saved);
        saved.setInsightText(result.text());
        saved.setInsightGeneratedAt(result.generatedAt());

        return bodyMetricsRepository.save(saved);
    }

    public BodyMetrics regenerateInsight(Long id) {
        BodyMetrics entry = bodyMetricsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        InsightService.InsightResult result = insightService.generateInsight(entry);
        entry.setInsightText(result.text());
        entry.setInsightGeneratedAt(result.generatedAt());

        return bodyMetricsRepository.save(entry);
    }

    public List<BodyMetrics> findAll() {
        return bodyMetricsRepository.findAll(Sort.by(Sort.Direction.ASC, "measuredOn"));
    }

    public BodyMetrics updateBodyMetrics(Long id, BodyMetrics updated) {
        BodyMetrics existing = bodyMetricsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existing.setMeasuredOn(updated.getMeasuredOn());
        existing.setWeightKg(updated.getWeightKg());
        existing.setMuscleMassKg(updated.getMuscleMassKg());
        existing.setWaterLiters(updated.getWaterLiters());
        existing.setBodyFatKg(updated.getBodyFatKg());
        existing.setBodyFatPct(updated.getBodyFatPct());

        return bodyMetricsRepository.save(existing);
    }

    public void delete(Long id) {
        bodyMetricsRepository.deleteById(id);
    }
}
