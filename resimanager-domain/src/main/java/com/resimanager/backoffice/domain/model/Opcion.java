package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Opción funcional dentro de un módulo.
 */
public record Opcion(
        Integer id,
        Integer moduloId,
        String nombre,
        String descripcion,
        Estatus estatus,
        AuditInfo audit
) {

    public Opcion {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}