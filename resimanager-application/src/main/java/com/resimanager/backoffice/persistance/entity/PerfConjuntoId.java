package com.resimanager.backoffice.persistance.entity;

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
public class PerfConjuntoId implements Serializable {
    private static final long serialVersionUID = 8518636323313863376L;
    @NotNull
    @Column(name = "pfc_conj_id", nullable = false)
    private Integer pfcConjid;

    @NotNull
    @Column(name = "pfc_prf_id", nullable = false)
    private Integer pfcPrfid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PerfConjuntoId entity = (PerfConjuntoId) o;
        return Objects.equals(this.pfcConjid, entity.pfcConjid) &&
                Objects.equals(this.pfcPrfid, entity.pfcPrfid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pfcConjid, pfcPrfid);
    }

}