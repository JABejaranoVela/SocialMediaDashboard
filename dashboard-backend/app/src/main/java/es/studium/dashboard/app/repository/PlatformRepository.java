package es.studium.dashboard.app.repository;

import es.studium.dashboard.app.model.Platform;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Integer> {
    Optional<Platform> findByPlatformName(String platformName);
}
