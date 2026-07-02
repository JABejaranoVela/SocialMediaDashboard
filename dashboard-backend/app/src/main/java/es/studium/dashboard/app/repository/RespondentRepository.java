package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.Respondent;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RespondentRepository extends JpaRepository<Respondent,Integer> {
    List<Respondent> findByUser_UsernameOrderByTimestampDesc(String username);
    Optional<Respondent> findByRespondentIdAndUser_Username(Integer respondentId, String username);
}
