package es.studium.dashboard.app.model;

import jakarta.persistence.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "respondent_organization")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RespondentOrganization implements Serializable {
    @EmbeddedId
    private RespondentOrganizationId id;

    // Relación con Respondent
    @ManyToOne
    @MapsId("respondentId")
    @JoinColumn(name = "respondent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Respondent respondent;

    // Relación con Organization
    @ManyToOne
    @MapsId("organizationId")
    @JoinColumn(name = "organization_id")
    private Organization organization;

    // Getters y setters
    public RespondentOrganizationId getId() {
        return id;
    }

    public void setId(RespondentOrganizationId id) {
        this.id = id;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public void setRespondent(Respondent respondent) {
        this.respondent = respondent;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
