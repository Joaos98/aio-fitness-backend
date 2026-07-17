package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.dto.BodyMetricsWithInsightDto;
import com.joaosousa.aiofitness.dto.InsightDto;
import com.joaosousa.aiofitness.entity.BodyMetrics;
import com.joaosousa.aiofitness.repository.BodyMetricsRepository;
import com.joaosousa.aiofitness.repository.WorkoutLogRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BodyMetricsService {
    private final BodyMetricsRepository bodyMetricsRepository;
    private final WorkoutLogRepository workoutLogRepository;

    public BodyMetricsService(BodyMetricsRepository bodyMetricsRepository, WorkoutLogRepository workoutLogRepository) {
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.workoutLogRepository = workoutLogRepository;
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

    public List<BodyMetricsWithInsightDto> getAllWithInsights() {
        List<BodyMetrics> allMetrics = bodyMetricsRepository.findAll(Sort.by("measuredOn"));

        if (allMetrics.isEmpty()) return List.of();

        List<BodyMetricsWithInsightDto> result = new ArrayList<>();

        // first entry never has an insight
        result.add(toDto(allMetrics.getFirst(), null, null));

        for (int i = 1; i < allMetrics.size(); i++) {
            BodyMetrics current = allMetrics.get(i);
            BodyMetrics previous = allMetrics.get(i - 1);

            int periodCount = workoutLogRepository.countDistinctWorkoutDates(
                    previous.getMeasuredOn(), current.getMeasuredOn()
            );

            double personalAverage = 0;
            for (int j = 1; j < i; j++) {
                personalAverage += workoutLogRepository.countDistinctWorkoutDates(
                        allMetrics.get(j - 1).getMeasuredOn(),
                        allMetrics.get(j).getMeasuredOn()
                );
            }

            // if this is the second measurement (i=1), there are no prior periods
            // so use the current period count as the baseline
            if (i == 1) {
                personalAverage = periodCount;
            } else {
                personalAverage = personalAverage / (i - 1);
            }

            // frequency status — ±17.5% neutral band
            double lowerBound = personalAverage * 0.825;
            String frequencyStatus = periodCount < lowerBound ? "below_average" : "at_or_above_average";

            String muscleMassDirection = current.getMuscleMassKg() >= previous.getMuscleMassKg()
                    ? "held_or_improved" : "declined";

            String bodyFatDirection = current.getBodyFatPct() <= previous.getBodyFatPct()
                    ? "held_or_improved" : "declined";

            InsightDto muscleMassInsight = new InsightDto(
                    periodCount, personalAverage, frequencyStatus, muscleMassDirection,
                    getMessageType(frequencyStatus, muscleMassDirection)
            );

            InsightDto bodyFatInsight = new InsightDto(
                    periodCount, personalAverage, frequencyStatus, bodyFatDirection,
                    getMessageType(frequencyStatus, bodyFatDirection)
            );

            result.add(toDto(current, muscleMassInsight, bodyFatInsight));
        }

        return result;
    }

    private String getMessageType(String frequencyStatus, String metricChangeDirection) {
        if (frequencyStatus.equals("below_average") && metricChangeDirection.equals("declined")) {
            return "expected_dip";
        } else if (frequencyStatus.equals("below_average") && metricChangeDirection.equals("held_or_improved")) {
            return "nice_surprise";
        } else if (frequencyStatus.equals("at_or_above_average") && metricChangeDirection.equals("declined")) {
            return "worth_investigating";
        } else {
            return "on_track";
        }
    }

    private BodyMetricsWithInsightDto toDto(BodyMetrics m, InsightDto muscleMassInsight, InsightDto bodyFatInsight) {
        return new BodyMetricsWithInsightDto(
                m.getId(), m.getMeasuredOn(), m.getWeightKg(), m.getMuscleMassKg(),
                m.getWaterLiters(), m.getBodyFatKg(), m.getBodyFatPct(),
                muscleMassInsight, bodyFatInsight
        );
    }
}
