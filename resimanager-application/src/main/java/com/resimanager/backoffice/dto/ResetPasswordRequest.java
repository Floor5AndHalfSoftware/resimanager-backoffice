package com.resimanager.backoffice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Petición de restablecimiento de contraseña con token.
 */
@Schema(name = "ResetPasswordRequest", description = "Restablece la contraseña usando el token recibido por email")
public record ResetPasswordRequest(
        @Schema(description = "Token de restablecimiento recibido en el enlace del email",
                example = "3Jm2Xy9pQr7bK1sV0aZcD8fGh4LmN6tR", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String token,

        @Schema(description = "Nueva contraseña (mínimo 8 caracteres)", example = "NuevaClave2026!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password
) {
}
