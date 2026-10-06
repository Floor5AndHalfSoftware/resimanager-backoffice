package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.port.in.PasswordRecoveryUseCase;
import com.resimanager.backoffice.dto.ForgotPasswordRequest;
import com.resimanager.backoffice.dto.HttpErrorInfoJson;
import com.resimanager.backoffice.dto.MessageResponse;
import com.resimanager.backoffice.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;
import static com.resimanager.backoffice.utils.Constants.FORGOT_PASSWORD_PATH;
import static com.resimanager.backoffice.utils.Constants.RESET_PASSWORD_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Recuperación de contraseña")
public class PasswordRecoveryController {

    private final PasswordRecoveryUseCase passwordRecoveryUseCase;

    @Operation(
            summary = "Solicitar restablecimiento de contraseña",
            description = """
                    Envía un enlace de restablecimiento al email indicado si corresponde a una
                    cuenta activa. Responde siempre `200` para no revelar qué correos existen.
                    Puede responder `429` si se excede el límite de solicitudes.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud procesada; devuelve un mensaje informativo"),
            @ApiResponse(responseCode = "400", description = "Email ausente o con formato inválido",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfoJson.class))),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes de restablecimiento",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfoJson.class)))
    })
    @SecurityRequirements
    @PostMapping(value = FORGOT_PASSWORD_PATH, produces = "application/json", consumes = "application/json")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordRecoveryUseCase.solicitar(request.email());
        return ResponseEntity.ok(new MessageResponse(
                "Si el correo existe, recibirás un enlace de restablecimiento."));
    }

    @Operation(
            summary = "Restablecer contraseña",
            description = """
                    Valida el token de restablecimiento y actualiza la contraseña. Al completarse,
                    cierra todas las sesiones activas del usuario. Responde `400` si el token es
                    inválido, expirado o ya usado, o si la contraseña no cumple la política.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida; devuelve un mensaje informativo"),
            @ApiResponse(responseCode = "400", description = "Token inválido/expirado/usado o contraseña no válida",
                    content = @Content(schema = @Schema(implementation = HttpErrorInfoJson.class)))
    })
    @SecurityRequirements
    @PostMapping(value = RESET_PASSWORD_PATH, produces = "application/json", consumes = "application/json")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordRecoveryUseCase.restablecer(request.token(), request.password());
        return ResponseEntity.ok(new MessageResponse("Contraseña restablecida correctamente."));
    }
}
