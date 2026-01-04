package es.studium.dashboard.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.studium.dashboard.app.auth.Users;

@Entity
@Table(name = "respondent")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Respondent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "respondent_id", nullable = false)
    private Integer respondentId;

    @Column(name = "ts", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "age", columnDefinition = "TINYINT UNSIGNED")
    private Integer age;

    @Column(name = "gender", length = 30)
    private String gender;

    @OneToOne(mappedBy = "respondent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Demographics demographics;

    @OneToOne(mappedBy = "respondent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SocialMediaUsage socialMediaUsage;

    @OneToOne(mappedBy = "respondent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MentalHealthMetrics mentalHealthMetrics;

    @ManyToMany
    @JoinTable(name = "respondent_organization", joinColumns = @JoinColumn(name = "respondent_id"), inverseJoinColumns = @JoinColumn(name = "organization_id"))
    private Set<Organization> organizations = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "respondent_platform", joinColumns = @JoinColumn(name = "respondent_id"), inverseJoinColumns = @JoinColumn(name = "platform_id"))
    private Set<Platform> platforms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public Respondent() {
    }

    // Getters y setters
    public Integer getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

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

    public Users getUser() {
        return user;
    }

    public Demographics getDemographics() {
        return demographics;
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }

    public SocialMediaUsage getSocialMediaUsage() {
        return socialMediaUsage;
    }

    public void setSocialMediaUsage(SocialMediaUsage socialMediaUsage) {
        this.socialMediaUsage = socialMediaUsage;
    }

    public MentalHealthMetrics getMentalHealthMetrics() {
        return mentalHealthMetrics;
    }

    public void setMentalHealthMetrics(MentalHealthMetrics mentalHealthMetrics) {
        this.mentalHealthMetrics = mentalHealthMetrics;
    }

    public Set<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
