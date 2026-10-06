package com.resimanager.backoffice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Petición de solicitud de restablecimiento de contraseña.
 */
@Schema(name = "ForgotPasswordRequest", description = "Solicitud para enviar un enlace de restablecimiento de contraseña")
public record ForgotPasswordRequest(
        @Schema(description = "Email de la cuenta a recuperar", example = "usuario@correo.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email
) {
}
