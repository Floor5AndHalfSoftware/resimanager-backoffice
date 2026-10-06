package com.resimanager.backoffice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * Respuesta genérica con un mensaje de resultado legible.
 */
@Builder
@Schema(name = "MessageResponse", description = "Respuesta con un mensaje de resultado")
public record MessageResponse(
        @Schema(description = "Mensaje de resultado legible",
                example = "Si el correo existe, recibirás un enlace de restablecimiento.")
        String message
) {
}
