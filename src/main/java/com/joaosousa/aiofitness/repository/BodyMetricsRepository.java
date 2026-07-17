package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.BodyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Long> {
}
