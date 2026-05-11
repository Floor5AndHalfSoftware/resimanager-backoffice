package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ConjuntoListResponse;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/conjuntos")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conjuntos", description = "Gestión de conjuntos residenciales y sus usuarios")
public class ConjuntoController {

    private final ConjuntoService conjuntoService;

    @Operation(summary = "Listar conjuntos", description = "Obtiene la lista de conjuntos con filtros opcionales")
    @GetMapping
    public ResponseEntity<ConjuntoListResponse> listarConjuntos(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Búsqueda por nombre o documento") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        log.debug("GET /conjuntos - estatus: {}, search: {}, page: {}, limit: {}", estatus, search, page, limit);
        return ResponseEntity.ok(conjuntoService.getConjuntos(estatus, search, page, limit));
    }

    @Operation(
            summary = "Obtener conjunto por ID",
            description = "Obtiene la información completa de un conjunto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conjunto encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conjunto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConjuntoDTO> getConjuntoById(
            @Parameter(description = "ID del conjunto")
            @PathVariable Integer id
    ) {
        log.debug("GET /conjuntos/{}", id);
        return ResponseEntity.ok(conjuntoService.getConjuntoById(id));
    }

    @Operation(summary = "Crear conjunto", description = "Crea un nuevo conjunto residencial")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conjunto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<ConjuntoDTO> createConjunto(
            @Valid @RequestBody CreateConjuntoRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /conjuntos - nombre: {}", request.nombre);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        ConjuntoDTO result = conjuntoService.createConjunto(request, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Actualizar conjunto", description = "Actualiza los datos de un conjunto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conjunto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conjunto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConjuntoDTO> updateConjunto(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateConjuntoRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /conjuntos/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        ConjuntoDTO result = conjuntoService.updateConjunto(id, request, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Inactivar conjunto", description = "Inactiva (soft-delete) un conjunto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conjunto inactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Conjunto no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteConjunto(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /conjuntos/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        return ResponseEntity.ok(conjuntoService.deleteConjunto(id, username, estacion));
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

            @Valid @RequestBody AsignarPerfilesRequest request,
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

    public record CreateConjuntoRequest(
            @NotBlank @Size(max = 20) String documento,
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 15) String telefono,
            @NotBlank @Size(max = 80) String email,
            @NotNull Integer persContactoId
    ) {}

    public record UpdateConjuntoRequest(
            @Size(max = 20) String documento,
            @Size(max = 80) String nombre,
            @Size(max = 15) String telefono,
            @Size(max = 80) String email,
            Integer persContactoId,
            @Size(min = 1, max = 1) String estatus
    ) {}
}
