package es.studium.dashboard.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "mental_health_metrics")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MentalHealthMetrics {

    @Id
    @Column(name = "respondent_id", nullable = false)
    private Integer respondentId;

    /**
     * Asociación uno-a-uno con Respondent.
     * Usamos @MapsId para que ‘respondentId’ sea tanto PK como FK.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "respondent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Respondent respondent;

    @Column(name = "easily_distracted_scale", columnDefinition = "TINYINT")
    private Integer easilyDistractedScale;

    @Column(name = "worry_intensity_scale", columnDefinition = "TINYINT")
    private Integer worryIntensityScale;

    @Column(name = "difficulty_concentrating", columnDefinition = "TINYINT")
    private Integer difficultyConcentrating;

    @Column(name = "social_comparison_frequency", columnDefinition = "TINYINT")
    private Integer socialComparisonFrequency;

    @Column(name = "comparison_feeling", columnDefinition = "TINYINT")
    private Integer comparisonFeeling;

    @Column(name = "validation_seeking_frequency", columnDefinition = "TINYINT")
    private Integer validationSeekingFrequency;

    @Column(name = "depressed_frequency", columnDefinition = "TINYINT")
    private Integer depressedFrequency;

    @Column(name = "interest_fluctuation_scale", columnDefinition = "TINYINT")
    private Integer interestFluctuationScale;

    @Column(name = "sleep_issue_scale", columnDefinition = "TINYINT")
    private Integer sleepIssueScale;

    public MentalHealthMetrics() {
        // Constructor por defecto para JPA
    }

    // Getters y setters
    public Integer getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public void setRespondent(Respondent respondent) {
        this.respondent = respondent;
    }

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
