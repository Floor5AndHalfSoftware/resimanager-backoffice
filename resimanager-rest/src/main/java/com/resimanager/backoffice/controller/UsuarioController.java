package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.PerfilesUsuario;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.dto.UpdateUsuarioRequest;
import com.resimanager.backoffice.dto.UsuarioDTO;
import com.resimanager.backoffice.dto.UsuarioListResponse;
import com.resimanager.backoffice.dto.UsuarioPerfilesResponse;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/usuarios")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;
    private final PersonaMapper personaMapper;

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
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;
        ResultadoPaginado<Persona> result = usuarioUseCase.obtenerUsuarios(estatusParam, search, page, limit);

        List<UsuarioDTO> data = result.datos().stream().map(personaMapper::toDTO).toList();
        return ResponseEntity.ok(UsuarioListResponse.builder()
                .data(data)
                .total(result.total())
                .page(result.pagina())
                .limit(result.limite())
                .build());
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id
    ) {
        Persona persona = usuarioUseCase.obtenerUsuario(id)
                .orElseThrow(() -> new com.resimanager.backoffice.exception.ResourceNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(personaMapper.toDTO(persona));
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            @Valid @RequestBody UpdateUsuarioRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Persona persona = usuarioUseCase.actualizarUsuario(id, request.nombre(), request.apellido(),
                request.telefono(), request.email(), estatus, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(personaMapper.toDTO(persona));
    }

    @Operation(summary = "Inactivar usuario", description = "Realiza un soft-delete cambiando el estatus a 'I'")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        usuarioUseCase.inactivarUsuario(id, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Usuario inactivado correctamente"));
    }

    @Operation(summary = "Perfiles de un usuario", description = "Obtiene los perfiles asignados al usuario agrupados por contexto (Administradora/Conjunto)")
    @GetMapping("/{id}/perfiles")
    public ResponseEntity<UsuarioPerfilesResponse> getPerfilesUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer id
    ) {
        PerfilesUsuario perfiles = usuarioUseCase.obtenerPerfilesDeUsuario(id);

        List<UsuarioPerfilesResponse.ContextoPerfilDTO> contextos = perfiles.contextos().stream()
                .map(c -> UsuarioPerfilesResponse.ContextoPerfilDTO.builder()
                        .tipo(c.tipo())
                        .entidad(UsuarioPerfilesResponse.EntidadDTO.builder()
                                .id(c.entidadId())
                                .nombre(c.entidadNombre())
                                .build())
                        .perfiles(c.perfiles().stream()
                                .map(p -> UsuarioPerfilesResponse.PerfilSimpleDTO.builder()
                                        .id(p.id())
                                        .nombre(p.nombre())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return ResponseEntity.ok(UsuarioPerfilesResponse.builder()
                .persona(personaMapper.toDTO(perfiles.persona()))
                .contextos(contextos)
                .build());
    }
}