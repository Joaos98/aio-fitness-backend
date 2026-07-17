package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkoutLogDto {
    private String type;
    private String colorHex;
    private Integer durationMinutes;
    private Integer calories;
}
