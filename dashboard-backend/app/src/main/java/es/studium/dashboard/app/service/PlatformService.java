package es.studium.dashboard.app.service;

import es.studium.dashboard.app.model.Platform;
import es.studium.dashboard.app.repository.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {
    private final PlatformRepository repo;

    public PlatformService(PlatformRepository repo) {
        this.repo = repo;
    }

    public List<Platform> findAll() {
        return repo.findAll();
    }

    public Optional<Platform> findById(Integer id) {
        return repo.findById(id);
    }

    public Platform save(Platform p) {
        return repo.save(p);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
