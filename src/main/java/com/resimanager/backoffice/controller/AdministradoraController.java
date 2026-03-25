package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.service.AdministradoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/administradoras")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administradoras", description = "Gestión de administradoras y sus usuarios")
public class AdministradoraController {

    private final AdministradoraService administradoraService;

    @Operation(
            summary = "Obtener administradora por ID",
            description = "Obtiene la información básica (id y nombre) de una administradora."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administradora encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Administradora no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAdministradoraById(
            @Parameter(description = "ID de la administradora")
            @PathVariable Integer id
    ) {
        log.debug("GET /administradoras/{}", id);
        Map<String, Object> result = administradoraService.getAdministradoraById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Listar usuarios de una administradora",
            description = """
                    Obtiene todos los usuarios activos pertenecientes a la administradora,
                    junto con los perfiles asignados a cada uno dentro de la misma.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Administradora no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<ContextoUsuariosResponse> getUsuarios(
            @Parameter(description = "ID de la administradora")
            @PathVariable Integer id
    ) {
        log.debug("GET /administradoras/{}/usuarios", id);
        ContextoUsuariosResponse response = administradoraService.getUsuarios(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Asignar perfiles a un usuario en la administradora",
            description = """
                    Asigna uno o más perfiles a un usuario dentro de la administradora.

                    Si un perfil ya estaba asignado pero inactivo, se reactiva.
                    Si ya está activo, se mantiene sin cambios.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfiles asignados exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Perfiles asignados correctamente\", \"perfiles_asignados\": 2, \"perfiles_reactivados\": 0}"
                    ))),
            @ApiResponse(responseCode = "404", description = "Administradora, usuario o perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/{admId}/usuarios/{usuarioId}/perfiles")
    public ResponseEntity<Map<String, Object>> asignarPerfiles(
            @Parameter(description = "ID de la administradora")
            @PathVariable Integer admId,

            @Parameter(description = "ID del usuario (persona)")
            @PathVariable Integer usuarioId,

            @RequestBody AsignarPerfilesRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /administradoras/{}/usuarios/{}/perfiles - Asignar {} perfiles",
                admId, usuarioId, request.getPerfiles() != null ? request.getPerfiles().size() : 0);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        Map<String, Object> result = administradoraService.asignarPerfiles(admId, usuarioId, request, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Remover perfil de un usuario en la administradora",
            description = """
                    Inactiva la asignación de un perfil específico a un usuario dentro de la administradora.

                    **Nota:** Esta operación no borra la asignación, solo la inactiva.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil removido exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Perfil removido correctamente\"}"
                    ))),
            @ApiResponse(responseCode = "404", description = "Asignación de perfil no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}")
    public ResponseEntity<Map<String, String>> removerPerfil(
            @Parameter(description = "ID de la administradora")
            @PathVariable Integer admId,

            @Parameter(description = "ID del usuario (persona)")
            @PathVariable Integer usuarioId,

            @Parameter(description = "ID del perfil a remover")
            @PathVariable Integer perfilId,

            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /administradoras/{}/usuarios/{}/perfiles/{} - Remover perfil",
                admId, usuarioId, perfilId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        Map<String, String> result = administradoraService.removerPerfil(admId, usuarioId, perfilId, username, estacion);
        return ResponseEntity.ok(result);
    }
}
