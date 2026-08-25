package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Perfil asignado a una persona dentro de una administradora.
 * Determina el rol del usuario en ese contexto.
 */
public record PerfilPersonaAdministradora(
        Integer personaId,
        Integer administradoraId,
        Integer perfilId,
        Estatus estatus,
        AuditInfo audit
) {

    public PerfilPersonaAdministradora {
        Objects.requireNonNull(personaId, "persona es obligatoria");
        Objects.requireNonNull(administradoraId, "administradora es obligatoria");
        Objects.requireNonNull(perfilId, "perfil es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}