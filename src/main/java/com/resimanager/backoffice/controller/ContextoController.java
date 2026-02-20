package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.CambioContextoRequest;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para manejo de contextos de usuario
 */
@RestController
@RequestMapping("/api/v1/contexto")
@RequiredArgsConstructor
@Slf4j
public class ContextoController {

    private final ContextoService contextoService;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Cambia el contexto activo del usuario
     * @param request Datos del contexto a activar
     * @return Nuevo token JWT con el contexto actualizado
     */
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
