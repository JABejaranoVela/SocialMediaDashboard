package es.studium.dashboard.app.service;

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
import es.studium.dashboard.app.repository.DemographicsRepository;
import es.studium.dashboard.app.repository.MentalHealthMetricsRepository;
import es.studium.dashboard.app.repository.OrganizationRepository;
import es.studium.dashboard.app.repository.PlatformRepository;
import es.studium.dashboard.app.repository.RespondentOrganizationRepository;
import es.studium.dashboard.app.repository.RespondentPlatformRepository;
import es.studium.dashboard.app.repository.RespondentRepository;
import es.studium.dashboard.app.repository.SocialMediaUsageRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RespondentService {

    private final RespondentRepository repo;
    @Autowired
    private RespondentRepository respondentRepository;
    @Autowired
    private DemographicsRepository demographicsRepository;
    @Autowired
    private SocialMediaUsageRepository socialMediaUsageRepository;
    @Autowired
    private MentalHealthMetricsRepository mentalHealthMetricsRepository;
    @Autowired
    private RespondentOrganizationRepository respondentOrganizationRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private RespondentPlatformRepository respondentPlatformRepository;
    @Autowired
    private PlatformRepository platformRepository;

    public RespondentService(RespondentRepository repo) {
        this.repo = repo;
    }

    public List<Respondent> findAll() {
        return repo.findAll();
    }

    public Optional<Respondent> findById(Integer id) {
        return repo.findById(id);
    }

    public Respondent save(Respondent r) {
        return repo.save(r);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @Transactional
    public Optional<Respondent> updateRespondent(Integer id, RespondentRegisterDto dto) {
        Optional<Respondent> optionalRespondent = respondentRepository.findById(id);
        if (optionalRespondent.isEmpty()) {
            return Optional.empty();
        }

        Respondent respondent = optionalRespondent.get();

        // Actualiza datos básicos
        respondent.setAge(dto.getAge());
        respondent.setGender(dto.getGender());
        respondent.setTimestamp(LocalDateTime.now());

        // 1. Actualizar Demographics
        Demographics demo = respondent.getDemographics();
        if (demo == null) {
            demo = new Demographics();
            demo.setRespondent(respondent);
        }
        demo.setRelationshipStatus(dto.getDemographics().getRelationshipStatus());
        demo.setOccupationStatus(dto.getDemographics().getOccupationStatus());
        demographicsRepository.save(demo);
        respondent.setDemographics(demo);

        // 2. Actualizar Social Media Usage
        SocialMediaUsage smu = respondent.getSocialMediaUsage();
        if (smu == null) {
            smu = new SocialMediaUsage();
            smu.setRespondent(respondent);
        }
        smu.setUsesSocialMedia(dto.getSocialMediaUsage().getUsesSocialMedia());
        smu.setDailyAverageTime(dto.getSocialMediaUsage().getDailyAverageTime());
        smu.setAimlessUsageFrequency(dto.getSocialMediaUsage().getAimlessUsageFrequency());
        smu.setDistractionFrequency(dto.getSocialMediaUsage().getDistractionFrequency());
        smu.setRestlessnessFrequency(dto.getSocialMediaUsage().getRestlessnessFrequency());
        socialMediaUsageRepository.save(smu);
        respondent.setSocialMediaUsage(smu);

        // 3. Actualizar Mental Health Metrics
        MentalHealthMetrics mhm = respondent.getMentalHealthMetrics();
        if (mhm == null) {
            mhm = new MentalHealthMetrics();
            mhm.setRespondent(respondent);
        }
        mhm.setEasilyDistractedScale(dto.getMentalHealthMetrics().getEasilyDistractedScale());
        mhm.setWorryIntensityScale(dto.getMentalHealthMetrics().getWorryIntensityScale());
        mhm.setDifficultyConcentrating(dto.getMentalHealthMetrics().getDifficultyConcentrating());
        mhm.setSocialComparisonFrequency(dto.getMentalHealthMetrics().getSocialComparisonFrequency());
        mhm.setComparisonFeeling(dto.getMentalHealthMetrics().getComparisonFeeling());
        mhm.setValidationSeekingFrequency(dto.getMentalHealthMetrics().getValidationSeekingFrequency());
        mhm.setDepressedFrequency(dto.getMentalHealthMetrics().getDepressedFrequency());
        mhm.setInterestFluctuationScale(dto.getMentalHealthMetrics().getInterestFluctuationScale());
        mhm.setSleepIssueScale(dto.getMentalHealthMetrics().getSleepIssueScale());
        mentalHealthMetricsRepository.save(mhm);
        respondent.setMentalHealthMetrics(mhm);

        // 4. (Opcional) Actualizar Organización
        // Puedes borrar las antiguas asociaciones primero si lo necesitas
        respondentOrganizationRepository.deleteByRespondent_RespondentId(respondent.getRespondentId());
        if (dto.getOrganizationName() != null && !dto.getOrganizationName().isEmpty()) {
            Organization org = organizationRepository.findByOrganizationName(dto.getOrganizationName())
                    .orElseGet(() -> {
                        Organization o = new Organization();
                        o.setOrganizationName(dto.getOrganizationName());
                        return organizationRepository.save(o);
                    });

            RespondentOrganization ro = new RespondentOrganization();
            ro.setId(new RespondentOrganizationId(respondent.getRespondentId(), org.getOrganizationId()));
            ro.setRespondent(respondent);
            ro.setOrganization(org);
            respondentOrganizationRepository.save(ro);
        }

        // 5. Actualizar Plataformas (N:M)
        respondentPlatformRepository.deleteByRespondent_RespondentId(respondent.getRespondentId());
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

        Respondent updated = respondentRepository.save(respondent);
        return Optional.of(updated);
    }
}
