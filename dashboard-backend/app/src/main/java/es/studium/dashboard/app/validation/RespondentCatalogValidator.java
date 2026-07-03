package es.studium.dashboard.app.validation;

import es.studium.dashboard.app.dto.RespondentRegisterDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class RespondentCatalogValidator {

    public void validate(RespondentRegisterDto dto) {
        if (dto == null) {
            throw new InvalidCatalogValueException("El cuerpo de la solicitud es obligatorio");
        }
        requireAllowed("gender", dto.getGender(), RespondentCatalogs.GENDERS);
        if (dto.getDemographics() == null) {
            throw new InvalidCatalogValueException("demographics es obligatorio");
        }
        requireAllowed("relationshipStatus", dto.getDemographics().getRelationshipStatus(),
                RespondentCatalogs.RELATIONSHIPS);
        requireAllowed("occupationStatus", dto.getDemographics().getOccupationStatus(),
                RespondentCatalogs.OCCUPATIONS);
        if (dto.getSocialMediaUsage() == null) {
            throw new InvalidCatalogValueException("socialMediaUsage es obligatorio");
        }
        requireAllowed("usesSocialMedia", dto.getSocialMediaUsage().getUsesSocialMedia(),
                RespondentCatalogs.SOCIAL_MEDIA_USE);
        requireAllowed("dailyAverageTime", dto.getSocialMediaUsage().getDailyAverageTime(),
                RespondentCatalogs.DAILY_TIMES);
        requireOptionalAllowed("organizationName", dto.getOrganizationName(), RespondentCatalogs.ORGANIZATIONS);
        requireAllAllowed("platforms", dto.getPlatforms(), RespondentCatalogs.PLATFORMS);
    }

    private void requireAllowed(String field, String value, Set<String> allowed) {
        if (value == null || !allowed.contains(value)) {
            throw invalid(field, value);
        }
    }

    private void requireOptionalAllowed(String field, String value, Set<String> allowed) {
        if (value != null && !value.isBlank() && !allowed.contains(value)) {
            throw invalid(field, value);
        }
    }

    private void requireAllAllowed(String field, List<String> values, Set<String> allowed) {
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value == null || !allowed.contains(value)) {
                throw invalid(field, value);
            }
        }
    }

    private InvalidCatalogValueException invalid(String field, String value) {
        return new InvalidCatalogValueException(
                "Valor no canónico para " + field + ": " + String.valueOf(value));
    }
}
