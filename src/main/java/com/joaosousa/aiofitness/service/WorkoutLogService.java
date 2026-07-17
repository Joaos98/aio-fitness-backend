package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.dto.HeatmapDayDto;
import com.joaosousa.aiofitness.dto.WorkoutLogDto;
import com.joaosousa.aiofitness.entity.WorkoutLog;
import com.joaosousa.aiofitness.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkoutLogService {
    private final WorkoutLogRepository workoutLogRepository;

    public WorkoutLogService(WorkoutLogRepository workoutLogRepository) {
        this.workoutLogRepository = workoutLogRepository;
    }

    public WorkoutLog save(WorkoutLog workoutLog) {
        return workoutLogRepository.save(workoutLog);
    }

    public List<WorkoutLog> findAll() {
        return workoutLogRepository.findAll();
    }

    public void delete(Long id) {
        workoutLogRepository.deleteById(id);
    }

    public List<HeatmapDayDto> getHeatmapData(LocalDate startDate, LocalDate endDate) {
        List<WorkoutLog> logs = workoutLogRepository.findByDateRangeFetched(startDate, endDate);

        Map<LocalDate, List<WorkoutLog>> byDate = logs.stream()
                .collect(Collectors.groupingBy(WorkoutLog::getLogDate));

        return byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<WorkoutLogDto> workoutDtos = entry.getValue().stream()
                            .map(wl -> new WorkoutLogDto(
                                    wl.getWorkoutType().getName(),
                                    wl.getWorkoutType().getColorHex(),
                                    wl.getDurationMinutes(),
                                    wl.getCalories()
                            ))
                            .toList();

                    return new HeatmapDayDto(entry.getKey(), workoutDtos);
                })
                .toList();
    }


}
