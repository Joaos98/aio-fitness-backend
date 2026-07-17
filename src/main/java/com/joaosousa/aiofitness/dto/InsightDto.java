package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InsightDto {
    private int workoutCountThisPeriod;
    private double personalAverage;
    private String frequencyStatus; // "below_average", "at_or_above_average"
    private String metricChangeDirection; // "declined", "held_or_improved"
    private String messageType; // "expected_dip", "nice_surprise", "worth_investigating", "on_track"
}