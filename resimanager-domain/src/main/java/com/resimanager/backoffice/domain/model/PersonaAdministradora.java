package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Relación persona-administradora: acceso de un usuario a una administradora.
 */
public record PersonaAdministradora(
        Integer personaId,
        Integer administradoraId,
        Estatus estatus,
        AuditInfo audit
) {

    public PersonaAdministradora {
        Objects.requireNonNull(personaId, "persona es obligatoria");
        Objects.requireNonNull(administradoraId, "administradora es obligatoria");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActiva() {
        return estatus.esActivo();
    }
}