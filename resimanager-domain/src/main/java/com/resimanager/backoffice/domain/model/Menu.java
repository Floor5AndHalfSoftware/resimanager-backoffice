package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Menú de navegación del sistema.
 */
public record Menu(
        Integer id,
        String nombre,
        String descripcion,
        String posicion,
        Estatus estatus,
        AuditInfo audit
) {

    public Menu {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}