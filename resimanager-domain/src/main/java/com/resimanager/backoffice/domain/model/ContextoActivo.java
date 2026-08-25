package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.TipoContexto;

import java.util.Objects;

/**
 * Contexto activo y validado de un usuario: el contexto y perfil desde los que opera.
 */
public record ContextoActivo(
        TipoContexto tipo,
        Integer entidadId,
        String entidadNombre,
        Integer perfilId,
        String perfilNombre,
        String perfilDescripcion
) {

    public ContextoActivo {
        Objects.requireNonNull(tipo, "tipo es obligatorio");
        Objects.requireNonNull(entidadId, "entidadId es obligatorio");
        Objects.requireNonNull(perfilId, "perfilId es obligatorio");
    }
}