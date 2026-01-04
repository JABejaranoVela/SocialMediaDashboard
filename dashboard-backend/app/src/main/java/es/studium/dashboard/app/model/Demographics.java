package es.studium.dashboard.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "demographics")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Demographics {

    @Id
    @Column(name = "respondent_id")
    private Integer respondentId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "respondent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Respondent respondent;

    @Column(name = "relationship_status", length = 50)
    private String relationshipStatus;

    @Column(name = "occupation_status", length = 50)
    private String occupationStatus;

    public Demographics() {
    }

    // getters y setters...
    public String getOccupationStatus() {
        return occupationStatus;
    }

    public Integer getRespondentId() {
        return respondentId;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public void setRespondent(Respondent respondent) {
        this.respondent = respondent;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setOccupationStatus(String occupationStatus) {
        this.occupationStatus = occupationStatus;
    }
}
