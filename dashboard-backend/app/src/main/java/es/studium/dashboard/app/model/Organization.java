package es.studium.dashboard.app.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "organization")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @Column(name = "organization_name", length = 100, unique = true)
    private String organizationName;

    @ManyToMany(mappedBy = "organizations")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Respondent> respondents = new HashSet<>();

    public Organization() {
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Set<Respondent> getRespondents() {
        return respondents;
    }

    public void setRespondents(Set<Respondent> respondents) {
        this.respondents = respondents;
    }
}
