package com.resimanager.backoffice.dto;

import lombok.Builder;

/**
 * DTO que representa el contexto activo del usuario
 * Se devuelve después de cambiar de contexto
 */
@Builder
public record ContextoActualDTO(
    String tipo,
    Integer entidadId,
    String entidadNombre,
    Integer perfilId,
    String perfilNombre,
    String perfilDescripcion
) {}
