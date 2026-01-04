package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.SocialMediaUsage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialMediaUsageRepository extends JpaRepository<SocialMediaUsage, Integer> {
    // Método de consulta automática por el nombre del campo
    long countByUsesSocialMedia(String usesSocialMedia);

    @Query("SELECT AVG(CAST(s.dailyAverageTime AS double)) FROM SocialMediaUsage s JOIN Respondent r ON s.respondentId = r.respondentId WHERE r.age BETWEEN :minAge AND :maxAge AND s.usesSocialMedia = 'Yes'")

    Double averageDailyTimeByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    List<SocialMediaUsage> findAllByRespondent_AgeBetweenAndUsesSocialMedia(Integer minAge, Integer maxAge,
            String usesSocialMedia);

    @Query("SELECT AVG(s.aimlessUsageFrequency) FROM SocialMediaUsage s")
    Double avgAimlessUsageFrequency();

    @Query("SELECT AVG(s.distractionFrequency) FROM SocialMediaUsage s")
    Double avgDistractionFrequency();

    @Query("SELECT AVG(s.restlessnessFrequency) FROM SocialMediaUsage s")
    Double avgRestlessnessFrequency();
}
