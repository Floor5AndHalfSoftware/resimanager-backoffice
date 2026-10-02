package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import com.resimanager.backoffice.dto.CambioContextoRequest;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.UserService;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.ACCESS_COOKIE_NAME;
import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;
import static com.resimanager.backoffice.utils.Constants.REFRESH_COOKIE_NAME;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/contexto")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contexto", description = "Cambio de contexto multi-tenant (Administradora / Conjunto)")
public class ContextoController {

    private final ContextoService contextoService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PersonaMapper personaMapper;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.security.access-token-ttl-minutes:30}")
    private long accessTokenTtlMinutes;

    @Value("${app.security.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

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
                    También se rota el refresh token de la sesión con el nuevo contexto.

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
    public ResponseEntity<?> cambiarContexto(
            @Valid @RequestBody CambioContextoRequest request,
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshTokenAnterior,
            HttpServletResponse response) {
        try {
            // Obtener la autenticación actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Usuario {} solicitando cambio de contexto a: tipo={}, entidadId={}, perfilId={}",
                    username, request.tipo(), request.entidadId(), request.perfilId());

            // Obtener el ID del usuario
            var personaOpt = userService.getUserByUsername(username);
            if (personaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }

            Integer personaId = personaOpt.get().getId();

            // Validar y construir el contexto
            ContextoActualDTO contextoActual = contextoService.validarYConstruirContexto(
                    personaId,
                    request.tipo(),
                    request.entidadId(),
                    request.perfilId()
            );

            // Construir UserInfo
            var persona = personaOpt.get();
            UserInfoDTO userInfo = personaMapper.toUserInfoDTO(persona);

            // Generar nuevo token con el contexto
            String newToken = jwtService.generateTokenWithContext(auth, userInfo, contextoActual);
            response.addCookie(accessCookie(newToken));

            // Rotar el refresh token con el nuevo contexto
            ContextoRefresh contextoRefresh = new ContextoRefresh(
                    contextoActual.tipo(), contextoActual.entidadId(), contextoActual.perfilId());
            ResultadoRotacionRefresh refresh = refreshTokenUseCase.rotar(refreshTokenAnterior, contextoRefresh)
                    .orElseGet(() -> refreshTokenUseCase.emitirNuevo(personaId, contextoRefresh));
            response.addCookie(refreshCookie(refresh.refreshToken(), refreshTokenTtlSeconds));

            log.info("Contexto cambiado exitosamente para usuario {}: {} (Cookie Secure: {}, SameSite: {})",
                    username, contextoActual, cookieSecure, cookieSecure ? "None" : "Lax");

            // Still return token in response for backward compatibility
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

    private Cookie accessCookie(String value) {
        Cookie cookie = new Cookie(ACCESS_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (accessTokenTtlMinutes * 60));
        cookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
        return cookie;
    }

    private Cookie refreshCookie(String value, long maxAgeSeconds) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAgeSeconds);
        cookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
        return cookie;
    }
}
