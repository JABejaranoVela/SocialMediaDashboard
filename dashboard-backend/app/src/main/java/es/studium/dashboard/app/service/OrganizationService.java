package es.studium.dashboard.app.service;

import es.studium.dashboard.app.model.Organization;
import es.studium.dashboard.app.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {
    private final OrganizationRepository repo;

    public OrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public List<Organization> findAll() {
        return repo.findAll();
    }

    public Optional<Organization> findById(Integer id) {
        return repo.findById(id);
    }

    public Organization save(Organization o) {
        return repo.save(o);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
