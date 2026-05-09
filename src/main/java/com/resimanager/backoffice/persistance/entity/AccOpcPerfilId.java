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
public class AccOpcPerfilId implements Serializable {
    private static final long serialVersionUID = -5006547744321359266L;
    @NotNull
    @Column(name = "aop_prf_id", nullable = false)
    private Integer aopPrfid;

    @NotNull
    @Column(name = "aop_mod_id", nullable = false)
    private Integer aopModid;

    @NotNull
    @Column(name = "aop_opc_id", nullable = false)
    private Integer aopOpcid;

    @NotNull
    @Column(name = "aop_acc_id", nullable = false)
    private Integer aopAccid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccOpcPerfilId entity = (AccOpcPerfilId) o;
        return Objects.equals(this.aopOpcid, entity.aopOpcid) &&
                Objects.equals(this.aopPrfid, entity.aopPrfid) &&
                Objects.equals(this.aopAccid, entity.aopAccid) &&
                Objects.equals(this.aopModid, entity.aopModid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aopOpcid, aopPrfid, aopAccid, aopModid);
    }

}