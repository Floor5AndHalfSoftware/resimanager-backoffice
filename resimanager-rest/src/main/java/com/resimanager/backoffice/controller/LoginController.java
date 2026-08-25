package com.resimanager.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

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
    
    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

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
            
            // Create HttpOnly cookie for the JWT token
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);  // Not accessible via JavaScript
            jwtCookie.setSecure(cookieSecure);    // Read from configuration (false for dev/HTTP, true for prod/HTTPS)
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            // Use SameSite=None for cross-domain (frontend in Vercel, backend in Koyeb)
            // Requires Secure=true (HTTPS only)
            jwtCookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
            response.addCookie(jwtCookie);
            
            log.debug("JWT cookie created with Secure={}, SameSite={}", 
                    cookieSecure, cookieSecure ? "None" : "Lax");
            
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
                    Invalida la sesión actual eliminando la cookie HttpOnly del JWT.
                    También limpia cualquier dato de sesión en el frontend.

                    **Nota:** Como JWT es stateless, esta operación solo elimina la cookie del lado del cliente.
                    El token sigue siendo válido hasta su expiración natural.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @SecurityRequirements
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        log.info("Logout - Cerrando sesión");

        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(cookieSecure);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setAttribute("SameSite", cookieSecure ? "None" : "Lax");
        response.addCookie(jwtCookie);

        log.debug("JWT cookie cleared (MaxAge=0) with Secure={}, SameSite={}", cookieSecure, cookieSecure ? "None" : "Lax");
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}

