package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.service.ConjuntoService;
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
@RequestMapping(value = API_VERSION_PATH + "/conjuntos")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conjuntos", description = "Gestión de conjuntos residenciales y sus usuarios")
public class ConjuntoController {

    private final ConjuntoService conjuntoService;

    @Operation(
            summary = "Obtener conjunto por ID",
            description = "Obtiene la información básica (id y nombre) de un conjunto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conjunto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conjunto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getConjuntoById(
            @Parameter(description = "ID del conjunto")
            @PathVariable Integer id
    ) {
        log.debug("GET /conjuntos/{}", id);
        Map<String, Object> result = conjuntoService.getConjuntoById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Listar usuarios de un conjunto",
            description = """
                    Obtiene todos los usuarios activos pertenecientes al conjunto,
                    junto con los perfiles asignados a cada uno dentro del mismo.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conjunto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<ContextoUsuariosResponse> getUsuarios(
            @Parameter(description = "ID del conjunto")
            @PathVariable Integer id
    ) {
        log.debug("GET /conjuntos/{}/usuarios", id);
        ContextoUsuariosResponse response = conjuntoService.getUsuarios(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Asignar perfiles a un usuario en el conjunto",
            description = """
                    Asigna uno o más perfiles a un usuario dentro del conjunto.

                    Si un perfil ya estaba asignado pero inactivo, se reactiva.
                    Si ya está activo, se mantiene sin cambios.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfiles asignados exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Perfiles asignados correctamente\", \"perfiles_asignados\": 2, \"perfiles_reactivados\": 0}"
                    ))),
            @ApiResponse(responseCode = "404", description = "Conjunto, usuario o perfil no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping("/{conjId}/usuarios/{usuarioId}/perfiles")
    public ResponseEntity<Map<String, Object>> asignarPerfiles(
            @Parameter(description = "ID del conjunto")
            @PathVariable Integer conjId,

            @Parameter(description = "ID del usuario (persona)")
            @PathVariable Integer usuarioId,

            @RequestBody AsignarPerfilesRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /conjuntos/{}/usuarios/{}/perfiles - Asignar {} perfiles",
                conjId, usuarioId, request.getPerfiles() != null ? request.getPerfiles().size() : 0);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        Map<String, Object> result = conjuntoService.asignarPerfiles(conjId, usuarioId, request, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Remover perfil de un usuario en el conjunto",
            description = """
                    Inactiva la asignación de un perfil específico a un usuario dentro del conjunto.

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
    @DeleteMapping("/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}")
    public ResponseEntity<Map<String, String>> removerPerfil(
            @Parameter(description = "ID del conjunto")
            @PathVariable Integer conjId,

            @Parameter(description = "ID del usuario (persona)")
            @PathVariable Integer usuarioId,

            @Parameter(description = "ID del perfil a remover")
            @PathVariable Integer perfilId,

            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /conjuntos/{}/usuarios/{}/perfiles/{} - Remover perfil",
                conjId, usuarioId, perfilId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        Map<String, String> result = conjuntoService.removerPerfil(conjId, usuarioId, perfilId, username, estacion);
        return ResponseEntity.ok(result);
    }
}
