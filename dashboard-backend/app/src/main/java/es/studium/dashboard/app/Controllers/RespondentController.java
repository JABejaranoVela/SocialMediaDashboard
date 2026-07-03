package es.studium.dashboard.app.Controllers;

import es.studium.dashboard.app.auth.Users;
import es.studium.dashboard.app.auth.UsersRepository;
import es.studium.dashboard.app.dto.RespondentRegisterDto;
import es.studium.dashboard.app.dto.RespondentResponseDto;
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
import es.studium.dashboard.app.validation.RespondentCatalogValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/respondents")
public class RespondentController {

    private final RespondentService service;
    private final RespondentRepository respondentRepository;
    private final SocialMediaUsageRepository socialMediaUsageRepository;
    private final MentalHealthMetricsRepository mentalHealthMetricsRepository;
    private final UsersRepository usersRepository;
    private final OrganizationRepository organizationRepository;
    private final PlatformRepository platformRepository;
    private final RespondentOrganizationRepository respondentOrganizationRepository;
    private final RespondentPlatformRepository respondentPlatformRepository;
    private final RespondentCatalogValidator catalogValidator;

    public RespondentController(
            RespondentService service,
            RespondentRepository respondentRepository,
            SocialMediaUsageRepository socialMediaUsageRepository,
            MentalHealthMetricsRepository mentalHealthMetricsRepository,
            UsersRepository usersRepository,
            OrganizationRepository organizationRepository,
            PlatformRepository platformRepository,
            RespondentOrganizationRepository respondentOrganizationRepository,
            RespondentPlatformRepository respondentPlatformRepository,
            RespondentCatalogValidator catalogValidator) {
        this.service = service;
        this.respondentRepository = respondentRepository;
        this.socialMediaUsageRepository = socialMediaUsageRepository;
        this.mentalHealthMetricsRepository = mentalHealthMetricsRepository;
        this.usersRepository = usersRepository;
        this.organizationRepository = organizationRepository;
        this.platformRepository = platformRepository;
        this.respondentOrganizationRepository = respondentOrganizationRepository;
        this.respondentPlatformRepository = respondentPlatformRepository;
        this.catalogValidator = catalogValidator;
    }

    @GetMapping
    public List<RespondentResponseDto> listAll(Authentication authentication) {
        return service.findAllByUsername(authentication.getName()).stream()
                .map(RespondentResponseDto::from)
                .toList();
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<RespondentResponseDto> getOne(
            @PathVariable Integer id,
            Authentication authentication) {
        return service.findByIdAndUsername(id, authentication.getName())
                .map(RespondentResponseDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RespondentRegisterDto dto, Authentication authentication) {
        catalogValidator.validate(dto);
        Users user = usersRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        Respondent respondent = new Respondent();
        respondent.setAge(dto.getAge());
        respondent.setGender(dto.getGender());
        respondent.setTimestamp(LocalDateTime.now());
        respondent.setUser(user);

        Demographics demographics = new Demographics();
        demographics.setRelationshipStatus(dto.getDemographics().getRelationshipStatus());
        demographics.setOccupationStatus(dto.getDemographics().getOccupationStatus());
        demographics.setRespondent(respondent);
        respondent.setDemographics(demographics);

        SocialMediaUsage socialMediaUsage = new SocialMediaUsage();
        socialMediaUsage.setUsesSocialMedia(dto.getSocialMediaUsage().getUsesSocialMedia());
        socialMediaUsage.setDailyAverageTime(dto.getSocialMediaUsage().getDailyAverageTime());
        socialMediaUsage.setAimlessUsageFrequency(dto.getSocialMediaUsage().getAimlessUsageFrequency());
        socialMediaUsage.setDistractionFrequency(dto.getSocialMediaUsage().getDistractionFrequency());
        socialMediaUsage.setRestlessnessFrequency(dto.getSocialMediaUsage().getRestlessnessFrequency());
        socialMediaUsage.setRespondent(respondent);
        socialMediaUsageRepository.save(socialMediaUsage);
        respondent.setSocialMediaUsage(socialMediaUsage);

        MentalHealthMetrics mentalHealthMetrics = new MentalHealthMetrics();
        mentalHealthMetrics.setEasilyDistractedScale(dto.getMentalHealthMetrics().getEasilyDistractedScale());
        mentalHealthMetrics.setWorryIntensityScale(dto.getMentalHealthMetrics().getWorryIntensityScale());
        mentalHealthMetrics.setDifficultyConcentrating(dto.getMentalHealthMetrics().getDifficultyConcentrating());
        mentalHealthMetrics.setSocialComparisonFrequency(dto.getMentalHealthMetrics().getSocialComparisonFrequency());
        mentalHealthMetrics.setComparisonFeeling(dto.getMentalHealthMetrics().getComparisonFeeling());
        mentalHealthMetrics.setValidationSeekingFrequency(dto.getMentalHealthMetrics().getValidationSeekingFrequency());
        mentalHealthMetrics.setDepressedFrequency(dto.getMentalHealthMetrics().getDepressedFrequency());
        mentalHealthMetrics.setInterestFluctuationScale(dto.getMentalHealthMetrics().getInterestFluctuationScale());
        mentalHealthMetrics.setSleepIssueScale(dto.getMentalHealthMetrics().getSleepIssueScale());
        mentalHealthMetrics.setRespondent(respondent);
        mentalHealthMetricsRepository.save(mentalHealthMetrics);
        respondent.setMentalHealthMetrics(mentalHealthMetrics);

        if (dto.getOrganizationName() != null && !dto.getOrganizationName().isEmpty()) {
            Organization organization = organizationRepository.findByOrganizationName(dto.getOrganizationName())
                    .orElseGet(() -> {
                        Organization newOrganization = new Organization();
                        newOrganization.setOrganizationName(dto.getOrganizationName());
                        return organizationRepository.save(newOrganization);
                    });
            RespondentOrganization link = new RespondentOrganization();
            link.setId(new RespondentOrganizationId(respondent.getRespondentId(), organization.getOrganizationId()));
            link.setRespondent(respondent);
            link.setOrganization(organization);
            respondentOrganizationRepository.save(link);
        }

        if (dto.getPlatforms() != null) {
            for (String platformName : dto.getPlatforms()) {
                Platform platform = platformRepository.findByPlatformName(platformName)
                        .orElseGet(() -> {
                            Platform newPlatform = new Platform();
                            newPlatform.setPlatformName(platformName);
                            return platformRepository.save(newPlatform);
                        });
                RespondentPlatform link = new RespondentPlatform();
                link.setId(new RespondentPlatformId(respondent.getRespondentId(), platform.getPlatformId()));
                link.setRespondent(respondent);
                link.setPlatform(platform);
                respondentPlatformRepository.save(link);
            }
        }

        return ResponseEntity.created(URI.create("/api/respondents/" + respondent.getRespondentId()))
                .body(RespondentResponseDto.from(respondent));
    }

    @GetMapping("/by-user")
    public List<RespondentResponseDto> getByUser(Authentication authentication) {
        return respondentRepository.findByUser_UsernameOrderByTimestampDesc(authentication.getName()).stream()
                .map(RespondentResponseDto::from)
                .toList();
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<RespondentResponseDto> update(
            @PathVariable Integer id,
            @RequestBody RespondentRegisterDto dto,
            Authentication authentication) {
        return service.updateRespondent(id, authentication.getName(), dto)
                .map(RespondentResponseDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication authentication) {
        if (!service.deleteByIdAndUsername(id, authentication.getName())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
