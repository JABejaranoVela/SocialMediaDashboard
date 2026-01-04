// RespondentOrganizationRepository.java
package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.RespondentOrganization;
import es.studium.dashboard.app.model.RespondentOrganizationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespondentOrganizationRepository extends JpaRepository<RespondentOrganization, RespondentOrganizationId> {
    void deleteByRespondent_RespondentId(Integer respondentId);
}
