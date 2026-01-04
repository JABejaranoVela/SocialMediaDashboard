package es.studium.dashboard.app.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "platform")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_id", nullable = false)
    private Integer platformId;

    @Column(name = "platform_name", length = 100, unique = true)
    private String platformName;

    @ManyToMany(mappedBy = "platforms")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Respondent> respondents = new HashSet<>();

    public Platform() {}

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Set<Respondent> getRespondents() {
        return respondents;
    }

    public void setRespondents(Set<Respondent> respondents) {
        this.respondents = respondents;
    }
}
