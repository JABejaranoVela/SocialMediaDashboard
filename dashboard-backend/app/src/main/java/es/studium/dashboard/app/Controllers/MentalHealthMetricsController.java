package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.model.MentalHealthMetrics;
import es.studium.dashboard.app.service.MentalHealthMetricsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mental-health")
public class MentalHealthMetricsController {

    private final MentalHealthMetricsService service;

    public MentalHealthMetricsController(MentalHealthMetricsService service) {
        this.service = service;
    }

    @GetMapping
    public List<MentalHealthMetrics> listAll() {
        return service.findAll();
    }

}
