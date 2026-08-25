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
public class OpcPerfilId implements Serializable {
    private static final long serialVersionUID = -7922848788965152493L;
    @NotNull
    @Column(name = "op_prf_id", nullable = false)
    private Integer opPrfid;

    @NotNull
    @Column(name = "op_mod_id", nullable = false)
    private Integer opModid;

    @NotNull
    @Column(name = "op_opc_id", nullable = false)
    private Integer opOpcid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OpcPerfilId entity = (OpcPerfilId) o;
        return Objects.equals(this.opPrfid, entity.opPrfid) &&
                Objects.equals(this.opOpcid, entity.opOpcid) &&
                Objects.equals(this.opModid, entity.opModid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opPrfid, opOpcid, opModid);
    }

}