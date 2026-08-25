package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Perfil asignado a una persona dentro de un conjunto.
 * Determina el rol del usuario en ese contexto.
 */
public record PerfilPersonaConjunto(
        Integer personaId,
        Integer conjuntoId,
        Integer perfilId,
        Estatus estatus,
        AuditInfo audit
) {

    public PerfilPersonaConjunto {
        Objects.requireNonNull(personaId, "persona es obligatoria");
        Objects.requireNonNull(conjuntoId, "conjunto es obligatorio");
        Objects.requireNonNull(perfilId, "perfil es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}