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
public class ModPerfilId implements Serializable {
    private static final long serialVersionUID = -531869305143932259L;
    @NotNull
    @Column(name = "mp_prf_id", nullable = false)
    private Integer mpPrfid;

    @NotNull
    @Column(name = "mp_mod_id", nullable = false)
    private Integer mpModid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ModPerfilId entity = (ModPerfilId) o;
        return Objects.equals(this.mpModid, entity.mpModid) &&
                Objects.equals(this.mpPrfid, entity.mpPrfid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mpModid, mpPrfid);
    }

}