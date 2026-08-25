package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.*;
import com.resimanager.backoffice.service.PerfilService;
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

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/perfiles")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Perfiles", description = "Gestión de perfiles de usuario (RBAC)")
public class PerfilController {

    private final PerfilService perfilService;

    @Operation(
            summary = "Listar perfiles",
            description = """
                    Obtiene una lista paginada de perfiles del sistema con filtros opcionales.
                    
                    Puede filtrar por:
                    - **estatus**: 'A' (Activo) o 'I' (Inactivo)
                    - **nivel**: 0 (Super Admin), 1 (Admin General), 2 (Admin Conjunto), 3 (Propietario/Residente)
                    - **search**: Búsqueda por nombre o descripción
                    
                    La respuesta incluye el contador de usuarios asignados a cada perfil.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfiles obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilListResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado")
    })
    @GetMapping
    public ResponseEntity<PerfilListResponse> listarPerfiles(
            @Parameter(description = "Filtrar por estatus (A/I)") 
            @RequestParam(required = false) String estatus,
            
            @Parameter(description = "Filtrar por nivel jerárquico (0-3)") 
            @RequestParam(required = false) Integer nivel,
            
            @Parameter(description = "Búsqueda por nombre o descripción") 
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Número de página (inicia en 1)") 
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Registros por página") 
            @RequestParam(required = false, defaultValue = "25") Integer limit
    ) {
        log.debug("GET /perfiles - estatus: {}, nivel: {}, search: {}, page: {}, limit: {}", 
                  estatus, nivel, search, page, limit);
        
        PerfilListResponse response = perfilService.getPerfiles(estatus, nivel, search, page, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Detalle de perfil",
            description = """
                    Obtiene el detalle completo de un perfil específico, incluyendo:
                    - Información básica del perfil
                    - Lista de módulos asignados
                    - Contador de usuarios asignados
                    - Fecha de creación
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle del perfil obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PerfilDetalleDTO> obtenerPerfil(
            @Parameter(description = "ID del perfil") 
            @PathVariable Integer id
    ) {
        log.debug("GET /perfiles/{}", id);
        
        PerfilDetalleDTO perfil = perfilService.getPerfilById(id);
        return ResponseEntity.ok(perfil);
    }

    @Operation(
            summary = "Crear perfil",
            description = """
                    Crea un nuevo perfil en el sistema.
                    
                    **Niveles jerárquicos:**
                    - 0: Super Administrador
                    - 1: Administrador General (Administradora)
                    - 2: Administrador de Conjunto
                    - 3: Propietario/Residente
                    
                    El perfil se crea en estado activo ('A') por defecto.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"id\": 6, \"nombre\": \"Contador\", \"descripcion\": \"Acceso a módulos financieros\", \"estatus\": \"A\", \"nivel\": 2}"
                            ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o perfil duplicado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<PerfilDTO> crearPerfil(
            @Valid @RequestBody CreatePerfilRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /perfiles - Crear perfil: {}", request.nombre());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();
        
        PerfilDTO perfil = perfilService.createPerfil(request, username, estacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(perfil);
    }

    @Operation(
            summary = "Actualizar perfil",
            description = """
                    Actualiza un perfil existente. Todos los campos son opcionales.
                    
                    Solo se actualizan los campos que se envíen en el request.
                    El sistema valida que no haya otro perfil con el mismo nombre.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PerfilDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PerfilDTO> actualizarPerfil(
            @Parameter(description = "ID del perfil") 
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePerfilRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /perfiles/{} - Actualizar perfil", id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();
        
        PerfilDTO perfil = perfilService.updatePerfil(id, request, username, estacion);
        return ResponseEntity.ok(perfil);
    }

    @Operation(
            summary = "Eliminar perfil",
            description = """
                    Elimina (inactiva) un perfil del sistema.
                    
                    **Nota:** Esta operación no borra físicamente el perfil, solo cambia su estatus a 'I' (Inactivo).
                    Las asignaciones existentes a usuarios se mantienen pero el perfil no estará disponible para nuevas asignaciones.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil inactivado exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Perfil inactivado correctamente\"}"
                    ))),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarPerfil(
            @Parameter(description = "ID del perfil") 
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /perfiles/{} - Eliminar perfil", id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();
        
        Map<String, String> result = perfilService.deletePerfil(id, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Asignar módulos a perfil",
            description = """
                    Asigna uno o más módulos a un perfil.
                    
                    Si un módulo ya estaba asignado pero inactivo, se reactiva.
                    Si un módulo ya está activo, se mantiene sin cambios.
                    
                    Todos los módulos especificados deben existir y estar activos.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulos asignados exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Módulos asignados correctamente\", \"modulos_asignados\": 3}"
                    ))),
            @ApiResponse(responseCode = "400", description = "Uno o más módulos no existen o están inactivos"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/{id}/modulos")
    public ResponseEntity<Map<String, Object>> asignarModulos(
            @Parameter(description = "ID del perfil") 
            @PathVariable Integer id,
            @Valid @RequestBody AsignarModulosRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /perfiles/{}/modulos - Asignar {} módulos", id, request.modulos().size());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();
        
        Map<String, Object> result = perfilService.asignarModulos(id, request, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Revocar módulo de perfil",
            description = """
                    Revoca (inactiva) la asignación de un módulo específico de un perfil.
                    
                    **Nota:** Esta operación no borra la asignación, solo la inactiva.
                    Los usuarios con este perfil perderán acceso al módulo especificado.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulo revocado exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Módulo removido del perfil\"}"
                    ))),
            @ApiResponse(responseCode = "404", description = "Perfil o módulo no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{id}/modulos/{moduloId}")
    public ResponseEntity<Map<String, String>> revocarModulo(
            @Parameter(description = "ID del perfil") 
            @PathVariable Integer id,
            
            @Parameter(description = "ID del módulo a revocar") 
            @PathVariable Integer moduloId,
            
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /perfiles/{}/modulos/{} - Revocar módulo", id, moduloId);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();
        
        Map<String, String> result = perfilService.revocarModulo(id, moduloId, username, estacion);
        return ResponseEntity.ok(result);
    }
}
