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
public class PersConjuntoId implements Serializable {
    private static final long serialVersionUID = -8936907586810601808L;
    @NotNull
    @Column(name = "pc_conj_id", nullable = false)
    private Integer pcConjid;

    @NotNull
    @Column(name = "pc_per_id", nullable = false)
    private Integer pcPerid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersConjuntoId entity = (PersConjuntoId) o;
        return Objects.equals(this.pcPerid, entity.pcPerid) &&
                Objects.equals(this.pcConjid, entity.pcConjid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pcPerid, pcConjid);
    }

}