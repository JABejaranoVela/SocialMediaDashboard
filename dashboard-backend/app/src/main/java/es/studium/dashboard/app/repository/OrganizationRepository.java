package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.Organization;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    Optional<Organization> findByOrganizationName(String organizationName);
}
