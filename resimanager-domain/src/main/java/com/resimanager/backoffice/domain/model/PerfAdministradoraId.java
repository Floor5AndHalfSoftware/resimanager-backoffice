package com.resimanager.backoffice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PerfAdministradoraId implements Serializable {
    private static final long serialVersionUID = -2957752942604347366L;
    @NotNull
    @Column(name = "pfa_adm_id", nullable = false)
    private Integer pfaAdmid;

    @NotNull
    @Column(name = "pfa_prf_id", nullable = false)
    private Integer pfaPrfid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PerfAdministradoraId entity = (PerfAdministradoraId) o;
        return Objects.equals(this.pfaAdmid, entity.pfaAdmid) &&
                Objects.equals(this.pfaPrfid, entity.pfaPrfid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pfaAdmid, pfaPrfid);
    }

}