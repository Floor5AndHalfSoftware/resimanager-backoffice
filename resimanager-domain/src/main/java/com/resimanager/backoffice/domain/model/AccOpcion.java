package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Asociación acción-opción: indica qué acciones están disponibles sobre una opción.
 */
public record AccOpcion(
        Integer id,
        Integer moduloId,
        Integer opcionId,
        Integer accionId,
        Integer codigoSeguridad,
        Estatus estatus,
        AuditInfo audit
) {

    public AccOpcion {
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}