package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.model.Organization;
import es.studium.dashboard.app.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Organization> listAll() {
        return service.findAll();
    }
}
