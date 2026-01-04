package es.studium.dashboard.app.model;

import java.io.Serializable;
import jakarta.persistence.*;

@Embeddable
public class RespondentOrganizationId implements Serializable {
    private Integer respondentId;
    private Integer organizationId;

    // Constructor vacío
    public RespondentOrganizationId() {}

    // Constructor con ambos campos
    public RespondentOrganizationId(Integer respondentId, Integer organizationId) {
        this.respondentId = respondentId;
        this.organizationId = organizationId;
    }

    // Getters y setters
    public Integer getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Integer respondentId) {
        this.respondentId = respondentId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    // equals y hashCode (importante para claves embebidas)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RespondentOrganizationId)) return false;
        RespondentOrganizationId that = (RespondentOrganizationId) o;
        return respondentId.equals(that.respondentId) &&
                organizationId.equals(that.organizationId);
    }

    @Override
    public int hashCode() {
        return respondentId.hashCode() + organizationId.hashCode();
    }
}
