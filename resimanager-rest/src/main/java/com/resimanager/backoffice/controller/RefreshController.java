package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.resimanager.backoffice.utils.Constants.ACCESS_COOKIE_NAME;
import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;
import static com.resimanager.backoffice.utils.Constants.REFRESH_COOKIE_NAME;
import static com.resimanager.backoffice.utils.Constants.REFRESH_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Renovación de sesión con refresh token")
public class RefreshController {

    private final RefreshTokenUseCase refreshTokenUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final ContextoService contextoService;
    private final JwtService jwtService;
    private final PersonaMapper personaMapper;

    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.security.access-token-ttl-minutes:30}")
    private long accessTokenTtlMinutes;

    @Value("${app.security.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    @Operation(
            summary = "Renovar sesión",
            description = """
                    Intercambia el refresh token (cookie HttpOnly `refresh`) por un nuevo access token.
                    El refresh token se rota en cada uso; si se reutiliza uno ya rotado, se revoca la sesión.
                    No requiere credenciales ni access token previo.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión renovada - devuelve nuevo access token"),
            @ApiResponse(responseCode = "401", description = "Refresh token ausente, inválido, expirado o revocado")
    })
    @SecurityRequirements
    @PostMapping(REFRESH_PATH)
    public ResponseEntity<?> refrescar(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        Optional<ResultadoRotacionRefresh> rotado = refreshTokenUseCase.rotar(refreshToken, null);
        if (rotado.isEmpty()) {
            log.debug("Refresh rechazado: token ausente, inválido o revocado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResultadoRotacionRefresh resultado = rotado.get();

        Optional<Persona> personaOpt = usuarioUseCase.obtenerUsuario(resultado.personaId());
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Persona persona = personaOpt.get();
        UserInfoDTO userInfo = personaMapper.toUserInfoDTO(persona);
        List<String> roles = contextoService.getRolesFromProfiles(persona.getId());

        ContextoActualDTO contexto = resultado.tieneContexto()
                ? ContextoActualDTO.builder()
                        .tipo(resultado.contextoTipo())
                        .entidadId(resultado.contextoEntidadId())
                        .perfilId(resultado.contextoPerfilId())
                        .build()
                : null;

        String accessToken = jwtService.generarTokenConContexto(userInfo, contexto, roles);

        response.addCookie(accessCookie(accessToken));
        response.addCookie(refreshCookie(resultado.refreshToken(), refreshTokenTtlSeconds));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", accessToken);
        body.put("type", "Bearer");
        if (contexto != null) {
            body.put("contexto", contexto);
        }

        log.debug("Sesión renovada para persona {}", persona.getId());
        return ResponseEntity.ok(body);
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
