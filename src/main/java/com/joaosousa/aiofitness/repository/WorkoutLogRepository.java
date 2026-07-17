package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    @Query("SELECT wl FROM WorkoutLog wl JOIN FETCH wl.workoutType WHERE wl.logDate BETWEEN :startDate AND :endDate ORDER BY wl.logDate")
    List<WorkoutLog> findByDateRangeFetched(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT wl.logDate FROM WorkoutLog wl WHERE wl.logDate BETWEEN :startDate AND :endDate")
    List<LocalDate> findDistinctWorkoutDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
