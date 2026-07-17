package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.BodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
    List<BodyMetrics> findAllByOrderByMeasuredOnAsc();
}
