package es.studium.dashboard.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "social_media_usage")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SocialMediaUsage {

    @Id
    @Column(name = "respondent_id", nullable = false)
    private Integer respondentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "respondent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Respondent respondent;

    @Column(name = "uses_social_media", length = 10)
    private String usesSocialMedia;

    @Column(name = "daily_average_time", length = 50)
    private String dailyAverageTime;

    @Column(name = "aimless_usage_frequency", columnDefinition = "TINYINT")
    private Integer aimlessUsageFrequency;

    @Column(name = "distraction_frequency", columnDefinition = "TINYINT")
    private Integer distractionFrequency;

    @Column(name = "restlessness_frequency", columnDefinition = "TINYINT")
    private Integer restlessnessFrequency;

    public SocialMediaUsage() {
        // Constructor por defecto
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
