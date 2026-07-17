package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BodyMetricsWithInsightDto {
    private Long id;
    private LocalDate measuredOn;
    private Double weightKg;
    private Double muscleMassKg;
    private Double waterLiters;
    private Double bodyFatKg;
    private Double bodyFatPct;
    private InsightDto muscleMassInsight;
    private InsightDto bodyFatInsight;
}