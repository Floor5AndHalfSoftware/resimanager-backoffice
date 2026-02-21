package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.CambioContextoRequest;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/contexto")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contexto", description = "Cambio de contexto multi-tenant (Administradora / Conjunto)")
public class ContextoController {

    private final ContextoService contextoService;
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(
            summary = "Cambiar contexto activo",
            description = """
                    Activa un contexto específico para el usuario autenticado y devuelve un **nuevo token JWT**
                    con los claims del contexto seleccionado.

                    El sistema soporta dos tipos de contexto:
                    - `ADMINISTRADORA` - Contexto de empresa administradora
                    - `CONJUNTO` - Contexto de conjunto/condominio residencial

                    **Usar el nuevo token** para todas las llamadas posteriores que requieran el contexto activo.
                    El `perfilId` devuelto debe usarse en el header `X-Perfil-Id` al consultar el menú.

                    Ejemplo de body:
                    ```json
                    { "tipo": "ADMINISTRADORA", "entidadId": 1, "perfilId": 2 }
                    ```
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contexto activado - devuelve nuevo token JWT con claims del contexto",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"token\": \"eyJ...\", \"type\": \"Bearer\", \"contexto\": {\"tipo\": \"ADMINISTRADORA\", \"entidadId\": 1, \"perfilId\": 2}}"
                    ))),
            @ApiResponse(responseCode = "400", description = "Contexto inválido o el usuario no tiene acceso a ese contexto/perfil"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o expirado")
    })
    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarContexto(@Valid @RequestBody CambioContextoRequest request) {
        try {
            // Obtener la autenticación actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            log.info("Usuario {} solicitando cambio de contexto a: tipo={}, entidadId={}, perfilId={}", 
                    username, request.getTipo(), request.getEntidadId(), request.getPerfilId());
            
            // Obtener el ID del usuario
            var personaOpt = userService.getUserByUsername(username);
            if (personaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }
            
            Integer personaId = personaOpt.get().getId();
            
            // Validar y construir el contexto
            ContextoActualDTO contextoActual = contextoService.validarYConstruirContexto(
                    personaId, 
                    request.getTipo(), 
                    request.getEntidadId(), 
                    request.getPerfilId()
            );
            
            // Construir UserInfo
            var persona = personaOpt.get();
            UserInfoDTO userInfo = UserInfoDTO.builder()
                    .id(persona.getId())
                    .usuario(persona.getPerUsuario())
                    .nombre(persona.getPerNombre())
                    .apellido(persona.getPerApellido())
                    .email(persona.getPerEMail())
                    .documento(persona.getPerDocIdent())
                    .build();
            
            // Generar nuevo token con el contexto
            String newToken = jwtService.generateTokenWithContext(auth, userInfo, contextoActual);
            
            log.info("Contexto cambiado exitosamente para usuario {}: {}", username, contextoActual);
            
            return ResponseEntity.ok(Map.of(
                    "token", newToken,
                    "type", "Bearer",
                    "contexto", contextoActual
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error al cambiar contexto: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al cambiar contexto", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
}
