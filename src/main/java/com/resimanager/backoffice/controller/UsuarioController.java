package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.UpdateUsuarioRequest;
import com.resimanager.backoffice.dto.UsuarioDTO;
import com.resimanager.backoffice.dto.UsuarioListResponse;
import com.resimanager.backoffice.dto.UsuarioPerfilesResponse;
import com.resimanager.backoffice.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/usuarios")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios", description = "Obtiene la lista de usuarios con filtros opcionales")
    @GetMapping
    public ResponseEntity<UsuarioListResponse> listarUsuarios(
            @Parameter(description = "Filtrar por estatus (A/I)")
            @RequestParam(required = false) String estatus,

            @Parameter(description = "Búsqueda por nombre, documento o email")
            @RequestParam(required = false) String search,

            @Parameter(description = "Número de página (inicia en 1)")
            @RequestParam(required = false, defaultValue = "1") Integer page,

            @Parameter(description = "Registros por página")
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        log.debug("GET /usuarios - estatus: {}, search: {}, page: {}, limit: {}", estatus, search, page, limit);
        return ResponseEntity.ok(usuarioService.getUsuarios(estatus, search, page, limit));
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id
    ) {
        log.debug("GET /usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            @RequestBody UpdateUsuarioRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /usuarios/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request, auth.getName(), httpRequest.getRemoteAddr()));
    }

    @Operation(summary = "Inactivar usuario", description = "Realiza un soft-delete cambiando el estatus a 'I'")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /usuarios/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(usuarioService.deleteUsuario(id, auth.getName(), httpRequest.getRemoteAddr()));
    }

    @Operation(summary = "Perfiles de un usuario", description = "Obtiene los perfiles asignados al usuario agrupados por contexto (Administradora/Conjunto)")
    @GetMapping("/{id}/perfiles")
    public ResponseEntity<UsuarioPerfilesResponse> getPerfilesUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id
    ) {
        log.debug("GET /usuarios/{}/perfiles", id);
        return ResponseEntity.ok(usuarioService.getUsuarioPerfiles(id));
    }
}
