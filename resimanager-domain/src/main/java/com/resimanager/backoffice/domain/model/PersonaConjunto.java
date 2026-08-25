package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Relación persona-conjunto: acceso de un usuario a un conjunto.
 */
public record PersonaConjunto(
        Integer personaId,
        Integer conjuntoId,
        Estatus estatus,
        AuditInfo audit
) {

    public PersonaConjunto {
        Objects.requireNonNull(personaId, "persona es obligatoria");
        Objects.requireNonNull(conjuntoId, "conjunto es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActiva() {
        return estatus.esActivo();
    }
}