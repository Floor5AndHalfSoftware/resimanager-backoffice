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
public class ConjPersAdministradoraId implements Serializable {
    private static final long serialVersionUID = -5690694376607227416L;
    @NotNull
    @Column(name = "cpa_adm_id", nullable = false)
    private Integer cpaAdmid;

    @NotNull
    @Column(name = "cpa_per_id", nullable = false)
    private Integer cpaPerid;

    @NotNull
    @Column(name = "cpa_conj_id", nullable = false)
    private Integer cpaConjid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ConjPersAdministradoraId entity = (ConjPersAdministradoraId) o;
        return Objects.equals(this.cpaAdmid, entity.cpaAdmid) &&
                Objects.equals(this.cpaConjid, entity.cpaConjid) &&
                Objects.equals(this.cpaPerid, entity.cpaPerid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpaAdmid, cpaConjid, cpaPerid);
    }

}