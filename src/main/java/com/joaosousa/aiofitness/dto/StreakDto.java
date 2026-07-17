package com.joaosousa.aiofitness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StreakDto {
    private int currentStreak;
    private int longestStreak;
}