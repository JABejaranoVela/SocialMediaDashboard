package es.studium.dashboard.app.Controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import es.studium.dashboard.app.repository.RespondentRepository;
import es.studium.dashboard.app.repository.SocialMediaUsageRepository;
import es.studium.dashboard.app.model.SocialMediaUsage;
import es.studium.dashboard.app.repository.MentalHealthMetricsRepository;
import es.studium.dashboard.app.repository.RespondentPlatformRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private RespondentRepository respondentRepository;

    @Autowired
    private SocialMediaUsageRepository socialMediaUsageRepository;

    @Autowired
    private MentalHealthMetricsRepository mentalHealthMetricsRepository;

    @Autowired
    private RespondentPlatformRepository respondentPlatformRepository;

    // Total de encuestados
    @GetMapping("/respondent/count")
    public Long getRespondentCount() {
        return respondentRepository.count();
    }

    // % que usan redes sociales
    @GetMapping("/social-media-users/percent")
    public Double getSocialMediaUsersPercent() {
        long validAnswers = 0;
        long users = 0;
        for (SocialMediaUsage usage : socialMediaUsageRepository.findAll()) {
            Optional<Boolean> normalized = normalizeSocialMediaUse(usage.getUsesSocialMedia());
            if (normalized.isPresent()) {
                validAnswers++;
                if (normalized.get()) {
                    users++;
                }
            }
        }
        return validAnswers > 0 ? (100.0 * users) / validAnswers : 0.0;
    }

    // Promedio escala distracción mental
    @GetMapping("/distraction/average")
    public Double getAverageDistraction() {
        Double avg = mentalHealthMetricsRepository.avgEasilyDistractedScale();
        return avg != null ? avg : 0.0;
    }

    @GetMapping("/social-media-usage/average-by-age")
    public Map<String, Object> getAverageUsageByAge() {
        int[] edges = { 18, 25, 35, 45, 60, 100 };
        String[] labels = { "18-24", "25-34", "35-44", "45-59", "60+" };
        List<Double> averages = new ArrayList<>();

        for (int i = 0; i < edges.length - 1; i++) {
            Integer minAge = edges[i];
            Integer maxAge = edges[i + 1] - 1;
            List<SocialMediaUsage> usageList = socialMediaUsageRepository
                    .findAllByRespondent_AgeBetween(minAge, maxAge);

            double avg = usageList.stream()
                    .filter(usage -> normalizeSocialMediaUse(usage.getUsesSocialMedia()).orElse(false))
                    .map(usage -> convertTimeStringToMinutes(usage.getDailyAverageTime()))
                    .flatMapToInt(OptionalInt::stream)
                    .average()
                    .orElse(0.0);
            averages.add(avg);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("labels", Arrays.asList(labels));
        res.put("values", averages);
        return res;
    }

    static Optional<Boolean> normalizeSocialMediaUse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return switch (value.trim()) {
            case "Yes", "Sí", "Si" -> Optional.of(true);
            case "No" -> Optional.of(false);
            default -> Optional.empty();
        };
    }

    public static OptionalInt convertTimeStringToMinutes(String value) {
        if (value == null) {
            return OptionalInt.empty();
        }
        return switch (value.trim()) {
            case "Less than an Hour", "Menos de 1 hora" -> OptionalInt.of(30);
            case "Between 1 and 2 hours", "Entre 1 y 2 horas" -> OptionalInt.of(90);
            case "Between 2 and 3 hours", "Entre 2 y 3 horas" -> OptionalInt.of(150);
            case "Between 3 and 4 hours", "Entre 3 y 4 horas" -> OptionalInt.of(210);
            case "Between 4 and 5 hours", "Entre 4 y 5 horas" -> OptionalInt.of(270);
            case "More than 5 hours", "Más de 5 horas" -> OptionalInt.of(330);
            default -> OptionalInt.empty();
        };
    }

    @GetMapping("/platform/bubble-count")
    public Map<String, Object> getPlatformBubbleData() {
        List<Object[]> data = respondentPlatformRepository.countUsersByPlatform();
        List<String> labels = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Object[] row : data) {
            labels.add((String) row[0]);
            counts.add(((Number) row[1]).intValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("counts", counts);
        return result;
    }

    @GetMapping("/mental-health/bullet-averages")
    public Map<String, Object> getMentalHealthBulletAverages() {
        List<String> labels = Arrays.asList(
                "Fácil de distraer", // easily_distracted_scale
                "Intensidad de preocupación", // worry_intensity_scale
                "Comparación social", // social_comparison_frequency
                "Búsqueda de validación", // validation_seeking_frequency
                "Frecuencia de depresión", // depressed_frequency
                "Fluctuación de interés", // interest_fluctuation_scale
                "Problemas de sueño", // sleep_issue_scale
                "Uso sin objetivo", // aimless_usage_frequency
                "Frecuencia de distracción", // distraction_frequency
                "Inquietud" // restlessness_frequency
        );

        List<Double> avgs = Arrays.asList(
                mentalHealthMetricsRepository.avgEasilyDistractedScale(),
                mentalHealthMetricsRepository.avgWorryIntensityScale(),
                mentalHealthMetricsRepository.avgSocialComparisonFrequency(),
                mentalHealthMetricsRepository.avgValidationSeekingFrequency(),
                mentalHealthMetricsRepository.avgDepressedFrequency(),
                mentalHealthMetricsRepository.avgInterestFluctuationScale(),
                mentalHealthMetricsRepository.avgSleepIssueScale(),
                socialMediaUsageRepository.avgAimlessUsageFrequency(),
                socialMediaUsageRepository.avgDistractionFrequency(),
                socialMediaUsageRepository.avgRestlessnessFrequency());

        Map<String, Object> resp = new HashMap<>();
        resp.put("labels", labels);
        resp.put("averages", avgs);
        return resp;
    }

}
