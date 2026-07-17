package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HeatmapDayDto {
    private LocalDate date;
    private List<WorkoutLogDto> workouts;
}