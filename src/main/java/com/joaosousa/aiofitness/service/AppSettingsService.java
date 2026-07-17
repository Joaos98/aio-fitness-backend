package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.AppSettings;
import com.joaosousa.aiofitness.repository.AppSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsService {
    private final AppSettingsRepository appSettingsRepository;

    public AppSettingsService(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    public AppSettings get() {
        return appSettingsRepository.findById(1L).orElseThrow();
    }

    public AppSettings update(AppSettings appSettings) {
        AppSettings existing = appSettingsRepository.findById(1L).orElseThrow();
        existing.setTargetWorkoutsPerWeek(appSettings.getTargetWorkoutsPerWeek());
        return appSettingsRepository.save(existing);
    }
}
