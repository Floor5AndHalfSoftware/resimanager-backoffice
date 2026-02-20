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
public class AccOpcionId implements Serializable {
    private static final long serialVersionUID = -3956880234697039228L;
    @NotNull
    @Column(name = "ao_mod_id", nullable = false)
    private Integer aoModid;

    @NotNull
    @Column(name = "ao_opc_id", nullable = false)
    private Integer aoOpcid;

    @NotNull
    @Column(name = "ao_acc_id", nullable = false)
    private Integer aoAccid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccOpcionId entity = (AccOpcionId) o;
        return Objects.equals(this.aoAccid, entity.aoAccid) &&
                Objects.equals(this.aoModid, entity.aoModid) &&
                Objects.equals(this.aoOpcid, entity.aoOpcid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aoAccid, aoModid, aoOpcid);
    }

}