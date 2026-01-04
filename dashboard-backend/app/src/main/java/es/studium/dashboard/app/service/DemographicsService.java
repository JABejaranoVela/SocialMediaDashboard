package es.studium.dashboard.app.service;

import es.studium.dashboard.app.model.Demographics;
import es.studium.dashboard.app.repository.DemographicsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DemographicsService {
    private final DemographicsRepository repo;

    public DemographicsService(DemographicsRepository repo) {
        this.repo = repo;
    }

    public List<Demographics> findAll() {
        return repo.findAll();
    }

    public Optional<Demographics> findById(Integer id) {
        return repo.findById(id);
    }

    public Demographics save(Demographics d) {
        return repo.save(d);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
