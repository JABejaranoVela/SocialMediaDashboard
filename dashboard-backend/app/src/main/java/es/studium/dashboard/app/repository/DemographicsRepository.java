package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.Demographics;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DemographicsRepository extends JpaRepository<Demographics, Integer> {
    @Query("SELECT d.occupationStatus, COUNT(d) FROM Demographics d GROUP BY d.occupationStatus")
    List<Object[]> countOccupationStatus();
}
