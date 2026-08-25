package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PerfilDetalle;
import com.resimanager.backoffice.domain.model.PerfilResumen;
import com.resimanager.backoffice.domain.model.PermisoModulo;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.PerfilUseCase;
import com.resimanager.backoffice.dto.AsignarModulosRequest;
import com.resimanager.backoffice.dto.CreatePerfilRequest;
import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.dto.PerfilDTO;
import com.resimanager.backoffice.dto.PerfilDetalleDTO;
import com.resimanager.backoffice.dto.PerfilListResponse;
import com.resimanager.backoffice.dto.PermisoDTO;
import com.resimanager.backoffice.dto.UpdatePerfilRequest;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.ModuloMapper;
import com.resimanager.backoffice.service.mapper.PerfilMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/perfiles")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Perfiles", description = "Gestión de perfiles de usuario (RBAC)")
public class PerfilController {

    private final PerfilUseCase perfilUseCase;
    private final PerfilMapper perfilMapper;
    private final ModuloMapper moduloMapper;

    @Operation(summary = "Listar perfiles", description = "Obtiene una lista paginada de perfiles del sistema con filtros opcionales.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfiles obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilListResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado")
    })
    @GetMapping
    public ResponseEntity<PerfilListResponse> listarPerfiles(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Filtrar por nivel jerárquico (0-3)") @RequestParam(required = false) Integer nivel,
            @Parameter(description = "Búsqueda por nombre o descripción") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "25") Integer limit
    ) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<PerfilResumen> pageResult =
                perfilUseCase.obtenerPerfiles(estatusParam, nivel, search, page, limit);

        List<PerfilDTO> perfilesDTO = pageResult.datos().stream()
                .map(r -> perfilMapper.toDTO(r.perfil()).toBuilder().usuariosAsignados(r.usuariosAsignados()).build())
                .toList();

        return ResponseEntity.ok(PerfilListResponse.builder()
                .data(perfilesDTO)
                .total(pageResult.total())
                .page(pageResult.pagina())
                .limit(pageResult.limite())
                .build());
    }

    @Operation(summary = "Detalle de perfil", description = "Obtiene el detalle completo de un perfil específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle del perfil obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PerfilDetalleDTO> obtenerPerfil(
            @Parameter(description = "ID del perfil") @PathVariable Integer id
    ) {
        PerfilDetalle detalle = perfilUseCase.obtenerDetallePerfil(id);
        Perfil perfil = detalle.perfil();

        List<ModuloDTO> modulosDTO = detalle.modulos().stream().map(moduloMapper::toDTO).toList();

        return ResponseEntity.ok(PerfilDetalleDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getPrfNombre())
                .descripcion(perfil.getPrfDescrip())
                .estatus(perfil.getPrfSts())
                .nivel(perfil.getPrfNivel())
                .modulos(modulosDTO)
                .permisos(groupPermissionsByModule(detalle.permisos()))
                .usuariosAsignados(detalle.usuariosAsignados())
                .fechaCreacion(perfil.getPrfFchHorCrea())
                .build());
    }

    @Operation(summary = "Crear perfil", description = "Crea un nuevo perfil en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o perfil duplicado")
    })
    @PostMapping
    public ResponseEntity<PerfilDTO> crearPerfil(
            @Valid @RequestBody CreatePerfilRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Perfil perfil = perfilUseCase.crearPerfil(request.nombre(), request.descripcion(),
                request.nivel(), auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(perfilMapper.toDTO(perfil));
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza un perfil existente. Todos los campos son opcionales.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDTO.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PerfilDTO> actualizarPerfil(
            @Parameter(description = "ID del perfil") @PathVariable Integer id,
            @Valid @RequestBody UpdatePerfilRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Perfil perfil = perfilUseCase.actualizarPerfil(id, request.nombre(), request.descripcion(),
                request.nivel(), estatus, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(perfilMapper.toDTO(perfil));
    }

    @Operation(summary = "Eliminar perfil", description = "Elimina (inactiva) un perfil del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil inactivado exitosamente",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"Perfil inactivado correctamente\"}"))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarPerfil(
            @Parameter(description = "ID del perfil") @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        perfilUseCase.inactivarPerfil(id, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Perfil inactivado correctamente"));
    }

    @Operation(summary = "Asignar módulos a perfil", description = "Asigna uno o más módulos a un perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulos asignados exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Módulos asignados correctamente\", \"modulos_asignados\": 3}"))),
            @ApiResponse(responseCode = "400", description = "Uno o más módulos no existen o están inactivos")
    })
    @PostMapping("/{id}/modulos")
    public ResponseEntity<Map<String, Object>> asignarModulos(
            @Parameter(description = "ID del perfil") @PathVariable Integer id,
            @Valid @RequestBody AsignarModulosRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        int asignados = perfilUseCase.asignarModulos(id, request.modulos(), auth.getName(),
                httpRequest.getRemoteAddr());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Módulos asignados correctamente");
        result.put("modulos_asignados", asignados);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Revocar módulo de perfil", description = "Revoca (inactiva) la asignación de un módulo específico de un perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulo revocado exitosamente",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"Módulo removido del perfil\"}"))),
            @ApiResponse(responseCode = "404", description = "Perfil o módulo no encontrado")
    })
    @DeleteMapping("/{id}/modulos/{moduloId}")
    public ResponseEntity<Map<String, String>> revocarModulo(
            @Parameter(description = "ID del perfil") @PathVariable Integer id,
            @Parameter(description = "ID del módulo a revocar") @PathVariable Integer moduloId,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        perfilUseCase.revocarModulo(id, moduloId, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Módulo removido del perfil"));
    }

    private List<PermisoDTO> groupPermissionsByModule(List<PermisoModulo> permisos) {
        Map<Integer, PermisoDTO> permisosMap = new HashMap<>();

        for (PermisoModulo permiso : permisos) {
            permisosMap.merge(permiso.moduloId(),
                new PermisoDTO(permiso.moduloId(), permiso.moduloNombre(), new ArrayList<>(List.of(permiso.accionNombre()))),
                (existing, ignored) -> {
                    existing.acciones().add(permiso.accionNombre());
                    return existing;
                });
        }

        return new ArrayList<>(permisosMap.values());
    }
}