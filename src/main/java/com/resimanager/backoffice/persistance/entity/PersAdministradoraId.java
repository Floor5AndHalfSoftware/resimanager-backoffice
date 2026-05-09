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
public class PersAdministradoraId implements Serializable {
    private static final long serialVersionUID = -7614469060147355624L;
    @NotNull
    @Column(name = "pa_adm_id", nullable = false)
    private Integer paAdmid;

    @NotNull
    @Column(name = "pa_per_id", nullable = false)
    private Integer paPerid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersAdministradoraId entity = (PersAdministradoraId) o;
        return Objects.equals(this.paPerid, entity.paPerid) &&
                Objects.equals(this.paAdmid, entity.paAdmid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paPerid, paAdmid);
    }

}