package com.joaosousa.aiofitness.dto;

import java.time.LocalDateTime;

public class InsightResponse {

    private final String text;
    private final LocalDateTime generatedAt;
    private final boolean fallback;

    public InsightResponse(String text, LocalDateTime generatedAt, boolean fallback) {
        this.text = text;
        this.generatedAt = generatedAt;
        this.fallback = fallback;
    }

    public String getText() { return text; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public boolean isFallback() { return fallback; }
}
