package com.joaosousa.aiofitness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaosousa.aiofitness.dto.GoalProgressDto;
import com.joaosousa.aiofitness.entity.BodyMetrics;
import com.joaosousa.aiofitness.entity.MetricType;
import com.joaosousa.aiofitness.repository.BodyMetricsRepository;
import com.joaosousa.aiofitness.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private static final Logger log = LoggerFactory.getLogger(InsightService.class);

    private static final String SYSTEM_PROMPT =
        "You are a personal fitness coach. The user tracks body metrics and workouts in a personal app. " +
        "Analyze their latest measurement in context. Write in 2nd person. Be concise, specific, and honest. " +
        "Maximum 2 paragraphs. " +
        "First paragraph: interpret what the data means. Don't list numbers — the user sees them already. " +
        "What patterns emerge? Is anything surprising? How does training connect to body composition changes? " +
        "Second paragraph: one actionable, specific suggestion the user can act on. " +
        "Keep it warm and direct. Never invent or guess.";

    private final BodyMetricsRepository bodyMetricsRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final GoalService goalService;

    @Value("${app.insight.api-key:}")
    private String apiKey;

    @Value("${app.insight.model:gemini-2.0-flash-lite}")
    private String model;

    public InsightService(BodyMetricsRepository bodyMetricsRepository,
                          WorkoutLogRepository workoutLogRepository,
                          GoalService goalService) {
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.goalService = goalService;
    }

    public InsightResult generateInsight(BodyMetrics latest) {
        List<BodyMetrics> allMetrics = bodyMetricsRepository.findAll(Sort.by(Sort.Direction.ASC, "measuredOn"));
        BodyMetrics previous = allMetrics.size() >= 2 ? allMetrics.get(allMetrics.size() - 2) : null;

        String prompt = buildPrompt(latest, previous, allMetrics);
        log.debug("Insight prompt:\n{}", prompt);

        try {
            String text = callGemini(prompt);
            return new InsightResult(text, latest.getMeasuredOn().atStartOfDay(), false);
        } catch (Exception e) {
            log.warn("Insight generation failed, using fallback: {}", e.getMessage());
            return new InsightResult(buildFallback(latest, previous), latest.getMeasuredOn().atStartOfDay(), true);
        }
    }

    // --- prompt builder ---

    private String buildPrompt(BodyMetrics latest, BodyMetrics previous, List<BodyMetrics> allMetrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("Here is the user's fitness data for analysis:\n\n");

        // Latest measurement
        sb.append("## Latest measurement (").append(latest.getMeasuredOn()).append(")\n");
        if (previous != null) {
            long daysSince = ChronoUnit.DAYS.between(previous.getMeasuredOn(), latest.getMeasuredOn());
            sb.append("(").append(daysSince).append(" days since the previous measurement)\n");
        }
        sb.append(metricLine("Weight", latest.getWeightKg(), previous, "kg"));
        sb.append(metricLine("Muscle mass", latest.getMuscleMassKg(), previous, "kg"));
        sb.append(metricLine("Body water", latest.getWaterLiters(), previous, "L"));
        sb.append(metricLine("Body fat mass", latest.getBodyFatKg(), previous, "kg"));
        sb.append(metricLine("Body fat %", latest.getBodyFatPct(), previous, "%"));

        // Trends — compute rates over recent window and all time
        if (allMetrics.size() >= 3) {
            sb.append("\n## Rate per month (recent 3 entries)\n");
            List<BodyMetrics> recent = allMetrics.subList(allMetrics.size() - 3, allMetrics.size());
            BodyMetrics first = recent.get(0);
            BodyMetrics last = recent.get(recent.size() - 1);
            long days = ChronoUnit.DAYS.between(first.getMeasuredOn(), last.getMeasuredOn());
            if (days > 0) {
                for (MetricType type : MetricType.values()) {
                    appendTrend(sb, type, first, last, days);
                }
            }

            if (allMetrics.size() >= 3 && allMetrics.size() != recent.size()) {
                sb.append("\n## Rate per month (all time)\n");
                BodyMetrics firstAll = allMetrics.get(0);
                long daysAll = ChronoUnit.DAYS.between(firstAll.getMeasuredOn(), latest.getMeasuredOn());
                if (daysAll > 0) {
                    for (MetricType type : MetricType.values()) {
                        appendTrend(sb, type, firstAll, latest, daysAll);
                    }
                }
            }
        }

        // Training since last measurement
        if (previous != null) {
            LocalDate since = previous.getMeasuredOn();
            LocalDate until = latest.getMeasuredOn();
            int workoutDays = workoutLogRepository.countDistinctWorkoutDates(since, until);
            long totalWorkouts = workoutLogRepository.countWorkouts(since, until);
            Integer maxDuration = workoutLogRepository.maxDuration(since, until);
            Double avgDuration = workoutLogRepository.averageDuration(since, until);
            String mostFreq = workoutLogRepository.mostFrequentType(since, until);

            sb.append("\n## Training (since last measurement)\n");
            sb.append("- Total workouts: ").append(totalWorkouts)
              .append(" across ").append(workoutDays).append(" days\n");
            if (avgDuration != null) {
                sb.append("- Average duration: ").append(Math.round(avgDuration)).append(" min\n");
            }
            if (maxDuration != null) {
                sb.append("- Longest session: ").append(maxDuration).append(" min\n");
            }
            if (mostFreq != null) {
                sb.append("- Most frequent type: ").append(mostFreq).append("\n");
            }
        }

        // Goals
        List<GoalProgressDto> goals = goalService.findAllWithProgress();
        List<GoalProgressDto> active = goals.stream()
                .filter(g -> "ACTIVE".equals(g.getStatus()))
                .collect(Collectors.toList());

        if (!active.isEmpty()) {
            sb.append("\n## Active goals\n");
            for (GoalProgressDto g : active) {
                sb.append("- ").append(goalLabel(g.getMetricType())).append(": target ")
                  .append(g.getTargetValue()).append(metricUnit(MetricType.valueOf(g.getMetricType())))
                  .append(", currently ").append(String.format("%.1f", g.getCurrentValue()));
                if (g.getStartValue() != null) {
                    sb.append(" (started at ").append(String.format("%.1f", g.getStartValue()))
                      .append(", ").append(progressLabel(g.getProgressPercent())).append(" progress)");
                }
                if (g.getEta() != null) {
                    sb.append(" — ETA ").append(g.getEta());
                } else if (g.getPaceStatus() != null) {
                    sb.append(" — pace: ").append(g.getPaceStatus());
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private void appendTrend(StringBuilder sb, MetricType type, BodyMetrics first, BodyMetrics last, long days) {
        double v1 = getMetricValue(first, type);
        double v2 = getMetricValue(last, type);
        double perMonth = (v2 - v1) / days * 30;
        String symbol = perMonth >= 0 ? "+" : "";
        sb.append("- ").append(type.name()).append(": ")
          .append(symbol).append(String.format("%.2f", perMonth)).append("/month")
          .append(" (").append(String.format("%.1f", v1)).append(" → ").append(String.format("%.1f", v2)).append(")\n");
    }

    private String goalLabel(String metricType) {
        return switch (metricType) {
            case "MUSCLE_MASS" -> "Muscle mass";
            case "BODY_FAT_KG" -> "Body fat (kg)";
            case "BODY_FAT_PCT" -> "Body fat %";
            case "WATER" -> "Body water";
            case "WEIGHT" -> "Weight";
            default -> metricType;
        };
    }

    private String metricLine(String label, Double value, BodyMetrics previous, String unit) {
        if (value == null) return "";
        String line = "- " + label + ": " + String.format("%.1f", value) + " " + unit;
        if (previous != null) {
            Double prev = getMetricValueByName(previous, label);
            if (prev != null) {
                double delta = value - prev;
                String sign = delta >= 0 ? "+" : "";
                line += " (" + sign + String.format("%.1f", delta) + ")";
            }
        }
        return line + "\n";
    }

    private String progressLabel(Double pct) {
        if (pct == null) return "no baseline";
        return String.format("%.0f%%", pct);
    }

    private String metricUnit(MetricType type) {
        return switch (type) {
            case WEIGHT, MUSCLE_MASS, BODY_FAT_KG -> "kg";
            case WATER -> "L";
            case BODY_FAT_PCT -> "%";
        };
    }

    private Double getMetricValue(BodyMetrics bm, MetricType type) {
        return switch (type) {
            case WEIGHT -> bm.getWeightKg();
            case MUSCLE_MASS -> bm.getMuscleMassKg();
            case WATER -> bm.getWaterLiters();
            case BODY_FAT_KG -> bm.getBodyFatKg();
            case BODY_FAT_PCT -> bm.getBodyFatPct();
        };
    }

    private Double getMetricValueByName(BodyMetrics bm, String label) {
        return switch (label) {
            case "Weight" -> bm.getWeightKg();
            case "Muscle mass" -> bm.getMuscleMassKg();
            case "Body water" -> bm.getWaterLiters();
            case "Body fat mass" -> bm.getBodyFatKg();
            case "Body fat %" -> bm.getBodyFatPct();
            default -> null;
        };
    }

    // --- fallback ---

    private String buildFallback(BodyMetrics latest, BodyMetrics previous) {
        StringBuilder sb = new StringBuilder();
        sb.append("Since your last measurement");
        if (previous != null) {
            sb.append(" on ").append(previous.getMeasuredOn()).append(",");
            boolean any = false;
            any |= appendDelta(sb, "weight", latest.getWeightKg(), previous.getWeightKg(), "kg");
            any |= appendDelta(sb, "muscle mass", latest.getMuscleMassKg(), previous.getMuscleMassKg(), "kg");
            any |= appendDelta(sb, "body fat %", latest.getBodyFatPct(), previous.getBodyFatPct(), "%");
            if (!any) sb.append(" your metrics held steady.");
        } else {
            sb.append(", your metrics were recorded.");
        }
        sb.append(" Keep logging consistently and a deeper analysis will appear here.");
        return sb.toString();
    }

    private boolean appendDelta(StringBuilder sb, String name, Double latest, Double previous, String unit) {
        if (latest == null || previous == null) return false;
        double delta = latest - previous;
        if (Math.abs(delta) < 0.01) return false;
        String direction = delta < 0 ? "dropped" : "increased";
        sb.append(" ").append(name).append(" ").append(direction).append(" ")
          .append(String.format("%.1f", Math.abs(delta))).append(" ").append(unit).append(",");
        return true;
    }

    // --- Gemini API ---

    private String callGemini(String prompt) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key not configured");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model
                + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
            "systemInstruction", Map.of(
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
            ),
            "contents", List.of(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt))
            )),
            "generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 2000
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
    }

    // --- result wrapper ---

    public record InsightResult(String text, LocalDateTime generatedAt, boolean fallback) {}
}
