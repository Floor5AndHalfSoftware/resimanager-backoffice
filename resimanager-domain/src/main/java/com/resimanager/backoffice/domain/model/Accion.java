package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Acción permitida sobre una opción (crear, listar, eliminar...).
 */
public record Accion(
        Integer id,
        String nombre,
        String descripcion,
        Estatus estatus,
        AuditInfo audit
) {

    public Accion {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}