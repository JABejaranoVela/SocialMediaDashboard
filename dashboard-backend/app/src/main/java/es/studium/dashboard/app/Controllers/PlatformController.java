package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.model.Platform;
import es.studium.dashboard.app.service.PlatformService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService service;

    public PlatformController(PlatformService service) {
        this.service = service;
    }

    @GetMapping
    public List<Platform> listAll() {
        return service.findAll();
    }
}
