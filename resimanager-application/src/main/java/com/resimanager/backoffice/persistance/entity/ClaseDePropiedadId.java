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
public class ClaseDePropiedadId implements Serializable {
    private static final long serialVersionUID = -1768824768336604487L;
    @NotNull
    @Column(name = "CdpID", nullable = false)
    private Integer cdpID;

    @NotNull
    @Column(name = "Cdp_ConjID", nullable = false)
    private Integer cdpConjid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClaseDePropiedadId entity = (ClaseDePropiedadId) o;
        return Objects.equals(this.cdpID, entity.cdpID) &&
                Objects.equals(this.cdpConjid, entity.cdpConjid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cdpID, cdpConjid);
    }

}