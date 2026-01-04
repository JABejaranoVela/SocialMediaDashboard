package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.RespondentPlatform;
import es.studium.dashboard.app.model.RespondentPlatformId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RespondentPlatformRepository extends JpaRepository<RespondentPlatform, RespondentPlatformId> {
    @Query("SELECT rp.platform.platformName, COUNT(rp.respondent.respondentId) FROM RespondentPlatform rp GROUP BY rp.platform.platformName")
    List<Object[]> countUsersByPlatform();
    void deleteByRespondent_RespondentId(Integer respondentId);
}
