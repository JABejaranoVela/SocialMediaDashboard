package es.studium.dashboard.app.service;

import es.studium.dashboard.app.model.SocialMediaUsage;
import es.studium.dashboard.app.repository.SocialMediaUsageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SocialMediaUsageService {
    private final SocialMediaUsageRepository repo;

    public SocialMediaUsageService(SocialMediaUsageRepository repo) {
        this.repo = repo;
    }

    public List<SocialMediaUsage> findAll() {
        return repo.findAll();
    }

    public Optional<SocialMediaUsage> findById(Integer id) {
        return repo.findById(id);
    }

    public SocialMediaUsage save(SocialMediaUsage s) {
        return repo.save(s);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
