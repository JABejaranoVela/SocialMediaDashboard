package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.auth.Users;
import es.studium.dashboard.app.auth.UsersRepository;
import es.studium.dashboard.app.dto.RespondentRegisterDto;
import es.studium.dashboard.app.model.Demographics;
import es.studium.dashboard.app.model.MentalHealthMetrics;
import es.studium.dashboard.app.model.Organization;
import es.studium.dashboard.app.model.Platform;
import es.studium.dashboard.app.model.Respondent;
import es.studium.dashboard.app.model.RespondentOrganization;
import es.studium.dashboard.app.model.RespondentOrganizationId;
import es.studium.dashboard.app.model.RespondentPlatform;
import es.studium.dashboard.app.model.RespondentPlatformId;
import es.studium.dashboard.app.model.SocialMediaUsage;
import es.studium.dashboard.app.repository.MentalHealthMetricsRepository;
import es.studium.dashboard.app.repository.OrganizationRepository;
import es.studium.dashboard.app.repository.PlatformRepository;
import es.studium.dashboard.app.repository.RespondentOrganizationRepository;
import es.studium.dashboard.app.repository.RespondentPlatformRepository;
import es.studium.dashboard.app.repository.RespondentRepository;
import es.studium.dashboard.app.repository.SocialMediaUsageRepository;
import es.studium.dashboard.app.service.RespondentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/respondents")
public class RespondentController {

    private final RespondentService service;

    @Autowired
    private RespondentRepository respondentRepository;
    @Autowired
    private SocialMediaUsageRepository socialMediaUsageRepository;
    @Autowired
    private MentalHealthMetricsRepository mentalHealthMetricsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private RespondentOrganizationRepository respondentOrganizationRepository;

    @Autowired
    private RespondentPlatformRepository respondentPlatformRepository;

    public RespondentController(RespondentService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("ENTRÓ EN EL MÉTODO GET /test");
        return ResponseEntity.ok("OK desde test");
    }

    @GetMapping
    public List<Respondent> listAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Respondent> getOne(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RespondentRegisterDto dto) {
        System.out.println("ENTRÓ EN EL MÉTODO POST DE RESPONDENTS");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Busca el usuario autenticado
        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        // 1. Guardar Respondent (principal)
        Respondent respondent = new Respondent();
        respondent.setAge(dto.getAge());
        respondent.setGender(dto.getGender());
        respondent.setTimestamp(LocalDateTime.now());
        respondent.setUser(user);

        // 2. Demographics como hijo
        Demographics demo = new Demographics();
        demo.setRelationshipStatus(dto.getDemographics().getRelationshipStatus());
        demo.setOccupationStatus(dto.getDemographics().getOccupationStatus());
        demo.setRespondent(respondent); // <-- Asocia el padre
        respondent.setDemographics(demo); // <-- Asocia el hijo (opcional, pero recomendable)

        // 3. Guardar Social Media Usage
        SocialMediaUsage smu = new SocialMediaUsage();
        smu.setUsesSocialMedia(dto.getSocialMediaUsage().getUsesSocialMedia());
        smu.setDailyAverageTime(dto.getSocialMediaUsage().getDailyAverageTime());
        smu.setAimlessUsageFrequency(dto.getSocialMediaUsage().getAimlessUsageFrequency());
        smu.setDistractionFrequency(dto.getSocialMediaUsage().getDistractionFrequency());
        smu.setRestlessnessFrequency(dto.getSocialMediaUsage().getRestlessnessFrequency());
        smu.setRespondent(respondent); // <-- AQUÍ TAMBIÉN
        socialMediaUsageRepository.save(smu);

        // 4. Guardar Mental Health Metrics
        MentalHealthMetrics mhm = new MentalHealthMetrics();
        mhm.setEasilyDistractedScale(dto.getMentalHealthMetrics().getEasilyDistractedScale());
        mhm.setWorryIntensityScale(dto.getMentalHealthMetrics().getWorryIntensityScale());
        mhm.setDifficultyConcentrating(dto.getMentalHealthMetrics().getDifficultyConcentrating());
        mhm.setSocialComparisonFrequency(dto.getMentalHealthMetrics().getSocialComparisonFrequency());
        mhm.setComparisonFeeling(dto.getMentalHealthMetrics().getComparisonFeeling());
        mhm.setValidationSeekingFrequency(dto.getMentalHealthMetrics().getValidationSeekingFrequency());
        mhm.setDepressedFrequency(dto.getMentalHealthMetrics().getDepressedFrequency());
        mhm.setInterestFluctuationScale(dto.getMentalHealthMetrics().getInterestFluctuationScale());
        mhm.setSleepIssueScale(dto.getMentalHealthMetrics().getSleepIssueScale());
        mhm.setRespondent(respondent); // <-- Y AQUÍ
        mentalHealthMetricsRepository.save(mhm); // <-- cascade

        // respondent = respondentRepository.save(respondent);

        // 5. (Opcional) Guardar Organización (solo si viene)
        if (dto.getOrganizationName() != null && !dto.getOrganizationName().isEmpty()) {
            Organization org = organizationRepository.findByOrganizationName(dto.getOrganizationName())
                    .orElseGet(() -> {
                        Organization o = new Organization();
                        o.setOrganizationName(dto.getOrganizationName());
                        return organizationRepository.save(o);
                    });

            // Tabla puente: RespondentOrganization
            RespondentOrganization ro = new RespondentOrganization();
            ro.setId(new RespondentOrganizationId(respondent.getRespondentId(), org.getOrganizationId()));
            ro.setRespondent(respondent);
            ro.setOrganization(org);
            respondentOrganizationRepository.save(ro);
        }

        // 6. Plataformas (N:M)
        if (dto.getPlatforms() != null) {
            for (String platformName : dto.getPlatforms()) {
                Platform platform = platformRepository.findByPlatformName(platformName)
                        .orElseGet(() -> {
                            Platform p = new Platform();
                            p.setPlatformName(platformName);
                            return platformRepository.save(p);
                        });

                RespondentPlatform rp = new RespondentPlatform();
                rp.setId(new RespondentPlatformId(respondent.getRespondentId(), platform.getPlatformId()));
                rp.setRespondent(respondent);
                rp.setPlatform(platform);
                respondentPlatformRepository.save(rp);
            }
        }

        // Devuelve el respondent creado (puedes devolver un DTO si quieres)
        return ResponseEntity
                .created(URI.create("/api/respondents/" + respondent.getRespondentId()))
                .body(respondent);
    }

    @GetMapping("/by-user")
    public List<Respondent> getByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return respondentRepository.findByUser_UsernameOrderByTimestampDesc(username);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RespondentRegisterDto dto) {
        return service.updateRespondent(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}