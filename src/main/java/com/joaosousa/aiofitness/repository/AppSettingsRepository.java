package com.joaosousa.aiofitness.repository;

import com.joaosousa.aiofitness.entity.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
