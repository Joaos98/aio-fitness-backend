package com.joaosousa.aiofitness.service;

import com.joaosousa.aiofitness.entity.AppSettings;
import com.joaosousa.aiofitness.repository.AppSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppSettingsService {
    private final AppSettingsRepository appSettingsRepository;

    public AppSettingsService(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    public AppSettings save(AppSettings appSettings) {
        return appSettingsRepository.save(appSettings);
    }

    public List<AppSettings> findAll() {
        return appSettingsRepository.findAll();
    }
}
