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
public class MenuItemId implements Serializable {
    private static final long serialVersionUID = 6177679664495162569L;
    @NotNull
    @Column(name = "MItID", nullable = false)
    private Integer mItID;

    @NotNull
    @Column(name = "MIt_MenuID", nullable = false)
    private Integer mitMenuid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MenuItemId entity = (MenuItemId) o;
        return Objects.equals(this.mItID, entity.mItID) &&
                Objects.equals(this.mitMenuid, entity.mitMenuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mItID, mitMenuid);
    }

}