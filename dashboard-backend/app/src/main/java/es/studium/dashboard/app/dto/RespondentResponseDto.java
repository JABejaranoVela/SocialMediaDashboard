package es.studium.dashboard.app.dto;

import es.studium.dashboard.app.auth.Users;
import es.studium.dashboard.app.model.Demographics;
import es.studium.dashboard.app.model.MentalHealthMetrics;
import es.studium.dashboard.app.model.Organization;
import es.studium.dashboard.app.model.Platform;
import es.studium.dashboard.app.model.Respondent;
import es.studium.dashboard.app.model.SocialMediaUsage;

import java.time.LocalDateTime;
import java.util.List;

public record RespondentResponseDto(
        Integer respondentId,
        LocalDateTime timestamp,
        Integer age,
        String gender,
        DemographicsDto demographics,
        SocialMediaUsageDto socialMediaUsage,
        MentalHealthMetricsDto mentalHealthMetrics,
        List<OrganizationDto> organizations,
        List<PlatformDto> platforms,
        UserSummaryDto user) {

    public static RespondentResponseDto from(Respondent respondent) {
        return new RespondentResponseDto(
                respondent.getRespondentId(), respondent.getTimestamp(), respondent.getAge(), respondent.getGender(),
                DemographicsDto.from(respondent.getDemographics()),
                SocialMediaUsageDto.from(respondent.getSocialMediaUsage()),
                MentalHealthMetricsDto.from(respondent.getMentalHealthMetrics()),
                respondent.getOrganizations().stream().map(OrganizationDto::from).toList(),
                respondent.getPlatforms().stream().map(PlatformDto::from).toList(),
                UserSummaryDto.from(respondent.getUser()));
    }

    public record DemographicsDto(String relationshipStatus, String occupationStatus) {
        private static DemographicsDto from(Demographics value) {
            return value == null ? null : new DemographicsDto(value.getRelationshipStatus(), value.getOccupationStatus());
        }
    }

    public record SocialMediaUsageDto(String usesSocialMedia, String dailyAverageTime,
            Integer aimlessUsageFrequency, Integer distractionFrequency, Integer restlessnessFrequency) {
        private static SocialMediaUsageDto from(SocialMediaUsage value) {
            return value == null ? null : new SocialMediaUsageDto(value.getUsesSocialMedia(), value.getDailyAverageTime(),
                    value.getAimlessUsageFrequency(), value.getDistractionFrequency(), value.getRestlessnessFrequency());
        }
    }

    public record MentalHealthMetricsDto(Integer easilyDistractedScale, Integer worryIntensityScale,
            Integer difficultyConcentrating, Integer socialComparisonFrequency, Integer comparisonFeeling,
            Integer validationSeekingFrequency, Integer depressedFrequency, Integer interestFluctuationScale,
            Integer sleepIssueScale) {
        private static MentalHealthMetricsDto from(MentalHealthMetrics value) {
            return value == null ? null : new MentalHealthMetricsDto(value.getEasilyDistractedScale(),
                    value.getWorryIntensityScale(), value.getDifficultyConcentrating(),
                    value.getSocialComparisonFrequency(), value.getComparisonFeeling(),
                    value.getValidationSeekingFrequency(), value.getDepressedFrequency(),
                    value.getInterestFluctuationScale(), value.getSleepIssueScale());
        }
    }

    public record OrganizationDto(String organizationName) {
        private static OrganizationDto from(Organization value) {
            return new OrganizationDto(value.getOrganizationName());
        }
    }

    public record PlatformDto(String platformName) {
        private static PlatformDto from(Platform value) {
            return new PlatformDto(value.getPlatformName());
        }
    }

    public record UserSummaryDto(String username, String role) {
        private static UserSummaryDto from(Users value) {
            return value == null ? null : new UserSummaryDto(value.getUsername(), value.getRole());
        }
    }
}
