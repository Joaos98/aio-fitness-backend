package com.joaosousa.aiofitness.controller;

import com.joaosousa.aiofitness.dto.SyncRequest;
import com.joaosousa.aiofitness.dto.SyncResponse;
import com.joaosousa.aiofitness.entity.ExerciseTypeMapping;
import com.joaosousa.aiofitness.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);

    private final SyncService syncService;
    private final String syncApiKey;

    public SyncController(SyncService syncService,
                          @Value("${app.sync.api-key}") String syncApiKey) {
        this.syncService = syncService;
        this.syncApiKey = syncApiKey;
        log.info("Sync API key loaded: [{}] (length={})", syncApiKey, syncApiKey != null ? syncApiKey.length() : 0);
    }

    @PostMapping
    public SyncResponse sync(@RequestBody SyncRequest request,
                             @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        log.info("Received sync request with X-API-Key: [{}] (length={})", apiKey, apiKey != null ? apiKey.length() : 0);
        log.info("Expected API key: [{}] (length={})", syncApiKey, syncApiKey != null ? syncApiKey.length() : 0);
        if (!syncApiKey.equals(apiKey)) {
            log.warn("API key mismatch: expected=[{}], got=[{}]", syncApiKey, apiKey);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing API key");
        }
        return syncService.sync(request);
    }

    @GetMapping("/mappings")
    public List<ExerciseTypeMapping> getMappings() {
        return syncService.getMappings();
    }

    @PostMapping("/mappings")
    public ExerciseTypeMapping addMapping(@RequestBody Map<String, Object> body) {
        int healthConnectType = ((Number) body.get("healthConnectType")).intValue();
        long workoutTypeId = ((Number) body.get("workoutTypeId")).longValue();
        return syncService.addMapping(healthConnectType, workoutTypeId);
    }

    @DeleteMapping("/mappings/{healthConnectType}")
    public void deleteMapping(@PathVariable int healthConnectType) {
        syncService.deleteMapping(healthConnectType);
    }
}
