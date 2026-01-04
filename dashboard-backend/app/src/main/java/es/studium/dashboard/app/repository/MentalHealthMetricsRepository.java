package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.MentalHealthMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MentalHealthMetricsRepository extends JpaRepository<MentalHealthMetrics, Integer> {
    // Usamos @Query para sacar el promedio de ese campo
    @Query("SELECT AVG(m.easilyDistractedScale) FROM MentalHealthMetrics m")
    Double avgEasilyDistractedScale();

    @Query("SELECT AVG(m.worryIntensityScale) FROM MentalHealthMetrics m")
    Double avgWorryIntensityScale();

    @Query("SELECT AVG(m.socialComparisonFrequency) FROM MentalHealthMetrics m")
    Double avgSocialComparisonFrequency();

    @Query("SELECT AVG(m.validationSeekingFrequency) FROM MentalHealthMetrics m")
    Double avgValidationSeekingFrequency();

    @Query("SELECT AVG(m.depressedFrequency) FROM MentalHealthMetrics m")
    Double avgDepressedFrequency();

    @Query("SELECT AVG(m.interestFluctuationScale) FROM MentalHealthMetrics m")
    Double avgInterestFluctuationScale();

    @Query("SELECT AVG(m.sleepIssueScale) FROM MentalHealthMetrics m")
    Double avgSleepIssueScale();
}
