package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Request DTO para cambio de contexto
 */
@Builder
public record CambioContextoRequest(
    @NotBlank(message = "El tipo de contexto es obligatorio")
    String tipo,
    @NotNull(message = "El ID de la entidad es obligatorio")
    Integer entidadId,
    @NotNull(message = "El ID del perfil es obligatorio")
    Integer perfilId
) {}
