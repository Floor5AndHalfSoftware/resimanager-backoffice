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
public class OpcionId implements Serializable {
    private static final long serialVersionUID = -7499113938290006938L;
    @NotNull
    @Column(name = "opcid", nullable = false)
    private Integer opcID;

    @NotNull
    @Column(name = "opc_modid", nullable = false)
    private Integer opcModid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OpcionId entity = (OpcionId) o;
        return Objects.equals(this.opcModid, entity.opcModid) &&
                Objects.equals(this.opcID, entity.opcID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcModid, opcID);
    }

}