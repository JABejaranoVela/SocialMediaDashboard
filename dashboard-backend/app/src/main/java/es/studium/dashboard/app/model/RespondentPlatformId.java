package es.studium.dashboard.app.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RespondentPlatformId implements Serializable {
    @Column(name = "respondent_id")
    private Integer respondentId;

    @Column(name = "platform_id")
    private Integer platformId;

    // --- constructor, equals y hashCode ---
    public RespondentPlatformId() {
    }

    public RespondentPlatformId(Integer respondentId, Integer platformId) {
        this.respondentId = respondentId;
        this.platformId = platformId;
    }

    public Integer getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RespondentPlatformId that = (RespondentPlatformId) o;
        return Objects.equals(respondentId, that.respondentId) && Objects.equals(platformId, that.platformId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(respondentId, platformId);
    }
}
