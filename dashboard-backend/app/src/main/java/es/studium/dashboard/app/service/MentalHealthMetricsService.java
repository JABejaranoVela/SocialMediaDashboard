package es.studium.dashboard.app.service;

import es.studium.dashboard.app.model.MentalHealthMetrics;
import es.studium.dashboard.app.repository.MentalHealthMetricsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MentalHealthMetricsService {

    private final MentalHealthMetricsRepository repo;

    public MentalHealthMetricsService(MentalHealthMetricsRepository repo) {
        this.repo = repo;
    }

    public List<MentalHealthMetrics> findAll() {
        return repo.findAll();
    }

    public Optional<MentalHealthMetrics> findById(Integer id) {
        return repo.findById(id);
    }

    public MentalHealthMetrics save(MentalHealthMetrics m) {
        return repo.save(m);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
