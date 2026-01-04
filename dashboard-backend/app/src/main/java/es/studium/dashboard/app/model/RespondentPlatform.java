package es.studium.dashboard.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "respondent_platform")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RespondentPlatform {

    @EmbeddedId
    private RespondentPlatformId id = new RespondentPlatformId();

    @ManyToOne
    @MapsId("respondentId")
    @JoinColumn(name = "respondent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Respondent respondent;

    @ManyToOne
    @MapsId("platformId")
    @JoinColumn(name = "platform_id")
    private Platform platform;

    // --- getters y setters ---
    public RespondentPlatformId getId() {
        return id;
    }

    public void setId(RespondentPlatformId id) {
        this.id = id;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public void setRespondent(Respondent respondent) {
        this.respondent = respondent;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
