package es.studium.dashboard.app.dto;

import java.util.List;

/**
 * DTO principal para recibir el registro de Respondent
 * Incluye clases estáticas anidadas para estructura ordenada
 */
public class RespondentRegisterDto {
    // Datos personales
    private Integer age;
    private String gender;

    // Demografía
    private DemographicsDto demographics;
    // Uso de redes sociales
    private SocialMediaUsageDto socialMediaUsage;

    // Métricas de salud mental
    private MentalHealthMetricsDto mentalHealthMetrics;

    // Organización (opcional)
    private String organizationName;

    // Plataformas (opcional, selección múltiple)
    private List<String> platforms;

    // Getters y setters

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public DemographicsDto getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicsDto demographics) {
        this.demographics = demographics;
    }

    public SocialMediaUsageDto getSocialMediaUsage() {
        return socialMediaUsage;
    }

    public void setSocialMediaUsage(SocialMediaUsageDto socialMediaUsage) {
        this.socialMediaUsage = socialMediaUsage;
    }

    public MentalHealthMetricsDto getMentalHealthMetrics() {
        return mentalHealthMetrics;
    }

    public void setMentalHealthMetrics(MentalHealthMetricsDto mentalHealthMetrics) {
        this.mentalHealthMetrics = mentalHealthMetrics;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    // -------------------- Clases internas --------------------

    /**
     * Demografía
     */
    public static class DemographicsDto {
        private String relationshipStatus;
        private String occupationStatus;

        public String getRelationshipStatus() {
            return relationshipStatus;
        }

        public void setRelationshipStatus(String relationshipStatus) {
            this.relationshipStatus = relationshipStatus;
        }

        public String getOccupationStatus() {
            return occupationStatus;
        }

        public void setOccupationStatus(String occupationStatus) {
            this.occupationStatus = occupationStatus;
        }
    }

    /**
     * Uso de redes sociales
     */
    public static class SocialMediaUsageDto {
        private String usesSocialMedia;
        private String dailyAverageTime;
        private Integer aimlessUsageFrequency;
        private Integer distractionFrequency;
        private Integer restlessnessFrequency;

        public String getUsesSocialMedia() {
            return usesSocialMedia;
        }

        public void setUsesSocialMedia(String usesSocialMedia) {
            this.usesSocialMedia = usesSocialMedia;
        }

        public String getDailyAverageTime() {
            return dailyAverageTime;
        }

        public void setDailyAverageTime(String dailyAverageTime) {
            this.dailyAverageTime = dailyAverageTime;
        }

        public Integer getAimlessUsageFrequency() {
            return aimlessUsageFrequency;
        }

        public void setAimlessUsageFrequency(Integer aimlessUsageFrequency) {
            this.aimlessUsageFrequency = aimlessUsageFrequency;
        }

        public Integer getDistractionFrequency() {
            return distractionFrequency;
        }

        public void setDistractionFrequency(Integer distractionFrequency) {
            this.distractionFrequency = distractionFrequency;
        }

        public Integer getRestlessnessFrequency() {
            return restlessnessFrequency;
        }

        public void setRestlessnessFrequency(Integer restlessnessFrequency) {
            this.restlessnessFrequency = restlessnessFrequency;
        }
    }

    /**
     * Métricas de salud mental
     */
    public static class MentalHealthMetricsDto {
        private Integer easilyDistractedScale;
        private Integer worryIntensityScale;
        private Integer difficultyConcentrating;
        private Integer socialComparisonFrequency;
        private Integer comparisonFeeling;
        private Integer validationSeekingFrequency;
        private Integer depressedFrequency;
        private Integer interestFluctuationScale;
        private Integer sleepIssueScale;

        public Integer getEasilyDistractedScale() {
            return easilyDistractedScale;
        }

        public void setEasilyDistractedScale(Integer easilyDistractedScale) {
            this.easilyDistractedScale = easilyDistractedScale;
        }

        public Integer getWorryIntensityScale() {
            return worryIntensityScale;
        }

        public void setWorryIntensityScale(Integer worryIntensityScale) {
            this.worryIntensityScale = worryIntensityScale;
        }

        public Integer getDifficultyConcentrating() {
            return difficultyConcentrating;
        }

        public void setDifficultyConcentrating(Integer difficultyConcentrating) {
            this.difficultyConcentrating = difficultyConcentrating;
        }

        public Integer getSocialComparisonFrequency() {
            return socialComparisonFrequency;
        }

        public void setSocialComparisonFrequency(Integer socialComparisonFrequency) {
            this.socialComparisonFrequency = socialComparisonFrequency;
        }

        public Integer getComparisonFeeling() {
            return comparisonFeeling;
        }

        public void setComparisonFeeling(Integer comparisonFeeling) {
            this.comparisonFeeling = comparisonFeeling;
        }

        public Integer getValidationSeekingFrequency() {
            return validationSeekingFrequency;
        }

        public void setValidationSeekingFrequency(Integer validationSeekingFrequency) {
            this.validationSeekingFrequency = validationSeekingFrequency;
        }

        public Integer getDepressedFrequency() {
            return depressedFrequency;
        }

        public void setDepressedFrequency(Integer depressedFrequency) {
            this.depressedFrequency = depressedFrequency;
        }

        public Integer getInterestFluctuationScale() {
            return interestFluctuationScale;
        }

        public void setInterestFluctuationScale(Integer interestFluctuationScale) {
            this.interestFluctuationScale = interestFluctuationScale;
        }

        public Integer getSleepIssueScale() {
            return sleepIssueScale;
        }

        public void setSleepIssueScale(Integer sleepIssueScale) {
            this.sleepIssueScale = sleepIssueScale;
        }
    }
}
