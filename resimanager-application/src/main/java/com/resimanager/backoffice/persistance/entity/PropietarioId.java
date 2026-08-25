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
public class PropietarioId implements Serializable {
    private static final long serialVersionUID = 7933902863518727569L;
    @NotNull
    @Column(name = "Ppt_ConjID", nullable = false)
    private Integer pptConjid;

    @NotNull
    @Column(name = "Ppt_PerID", nullable = false)
    private Integer pptPerid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PropietarioId entity = (PropietarioId) o;
        return Objects.equals(this.pptPerid, entity.pptPerid) &&
                Objects.equals(this.pptConjid, entity.pptConjid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pptPerid, pptConjid);
    }

}