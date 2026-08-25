package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Módulo asignado a un perfil.
 * Determina a qué módulos del sistema tiene acceso un perfil.
 */
public record ModuloPerfil(
        Integer perfilId,
        Integer moduloId,
        Estatus estatus,
        AuditInfo audit
) {

    public ModuloPerfil {
        Objects.requireNonNull(perfilId, "perfil es obligatorio");
        Objects.requireNonNull(moduloId, "módulo es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}