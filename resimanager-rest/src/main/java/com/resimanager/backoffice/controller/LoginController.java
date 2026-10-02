package com.resimanager.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import com.resimanager.backoffice.dto.*;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.UserService;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.ACCESS_COOKIE_NAME;
import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;
import static com.resimanager.backoffice.utils.Constants.LOGOUT_PATH;
import static com.resimanager.backoffice.utils.Constants.REFRESH_COOKIE_NAME;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Login y gestión de sesión de usuario")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final ContextoService contextoService;
    private final UserService userService;
    private final PersonaMapper personaMapper;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.security.access-token-ttl-minutes:30}")
    private long accessTokenTtlMinutes;

    @Value("${app.security.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Autentica al usuario y devuelve un token JWT junto con los contextos disponibles.

                    **Importante:** La contraseña debe enviarse codificada en Base64.

                    Ejemplos de contraseñas en Base64:
                    - `Admin2024!` → `QWRtaW4yMDI0IQ==`
                    - `Carlos2024!` → `Q2FybG9zMjAyNCE=`
                    - `Maria2024!` → `TWFyaWEyMDI0IQ==`

                    Si el usuario tiene **un solo contexto**, puedes operar directamente con el token devuelto.
                    Si tiene **múltiples contextos**, debes llamar a `/v1/contexto/cambiar` para activar uno.

                    Se emiten dos cookies HttpOnly: `jwt` (access token) y `refresh` (refresh token).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso - devuelve token JWT y contextos disponibles",
                    content = @Content(schema = @Schema(implementation = LoginResponseJson.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content(examples = @ExampleObject(value = "{\"error\": \"Credenciales inválidas\"}"))),
            @ApiResponse(responseCode = "400", description = "Petición mal formada - falta usuario o contraseña")
    })
    @SecurityRequirements
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<LoginResponseJson> login(
            @Valid @RequestBody @NotNull LoginRequestJson loginRequestJson,
            HttpServletResponse response) {
        log.info("Login attempt for user: {}", loginRequestJson.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestJson.username(), loginRequestJson.password())
        );

        if (authentication.isAuthenticated()) {
            // Get user data from database
            var persona = userService.getUserByUsername(loginRequestJson.username())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));

            // Build UserInfoDTO for JWT claims
            UserInfoDTO userInfo = personaMapper.toUserInfoDTO(persona);

            // Generate JWT token with user info in claims
            var token = jwtService.generateTokenWithUserInfo(authentication, userInfo);

            // Create HttpOnly access cookie
            response.addCookie(accessCookie(token));

            // Emit refresh token and set its HttpOnly cookie
            ResultadoRotacionRefresh refresh = refreshTokenUseCase.emitirNuevo(persona.getId(), ContextoRefresh.ninguno());
            response.addCookie(refreshCookie(refresh.refreshToken(), refreshTokenTtlSeconds));

            // Get available contexts for the user
            List<ContextoDTO> contextos = contextoService.getContextosDisponibles(persona.getId());

            log.info("Login successful for user: {} (ID: {}) with {} contexts",
                    persona.getPerUsuario(), persona.getId(), contextos.size());

            // Still return token in response for backward compatibility and mobile apps
            return ResponseEntity.ok().body(LoginResponseJson.builder()
                    .token(token)
                    .type("Bearer")
                    .usuario(userInfo)
                    .contextosDisponibles(contextos)
                    .build());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
    }

    @Operation(
            summary = "Cerrar sesión",
            description = """
                    Revoca en servidor el refresh token de la sesión y limpia las cookies HttpOnly
                    `jwt` y `refresh`.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @SecurityRequirements
    @PostMapping(LOGOUT_PATH)
    public ResponseEntity<Map<String, String>> logout(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {
        log.info("Logout - Cerrando sesión");

        refreshTokenUseCase.revocar(refreshToken);

        response.addCookie(accessCookie(null));
        response.addCookie(refreshCookie(null, 0));

        log.debug("Cookies de sesión limpiadas y refresh revocado");
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }

    private Cookie accessCookie(String value) {
        Cookie cookie = new Cookie(ACCESS_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(value == null ? 0 : (int) (accessTokenTtlMinutes * 60));
        cookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
        return cookie;
    }

    private Cookie refreshCookie(String value, long maxAgeSeconds) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        // Path "/" (igual que el access token) para que logout y cambio de contexto
        // puedan leer el refresh y revocarlo en servidor.
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAgeSeconds);
        cookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
        return cookie;
    }
}
