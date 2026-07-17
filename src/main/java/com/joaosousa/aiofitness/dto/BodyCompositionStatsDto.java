package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BodyCompositionStatsDto {
    private Double weightChangeKg;        // null if fewer than 2 measurements
    private Double muscleMassChangeKg;    // null if fewer than 2 measurements
    private Double bodyFatPctChange;      // null if fewer than 2 measurements
    private long totalMeasurements;
}
