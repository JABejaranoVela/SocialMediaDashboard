package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.model.SocialMediaUsage;
import es.studium.dashboard.app.service.SocialMediaUsageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-media-usage")
public class SocialMediaUsageController {
    private final SocialMediaUsageService service;

    public SocialMediaUsageController(SocialMediaUsageService service) {
        this.service = service;
    }

    @GetMapping
    public List<SocialMediaUsage> listAll() {
        return service.findAll();
    }
}
