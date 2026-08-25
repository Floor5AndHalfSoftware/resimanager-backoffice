package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.PropietarioDTO;
import com.resimanager.backoffice.dto.PropietarioListResponse;
import com.resimanager.backoffice.service.PropietarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping(value = API_VERSION_PATH + "/propietarios")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Propietarios", description = "Gestión de propietarios (asignación persona-propiedad en conjunto)")
public class PropietarioController {

    private final PropietarioService propietarioService;

    @Operation(summary = "Listar propietarios", description = "Obtiene la lista de propietarios con filtros opcionales")
    @GetMapping
    public ResponseEntity<PropietarioListResponse> listarPropietarios(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Filtrar por ID del conjunto") @RequestParam(required = false) Integer conjuntoId,
            @Parameter(description = "Búsqueda por persona") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") @Min(1) Integer limit
    ) {
        log.debug("GET /propietarios - estatus: {}, conjuntoId: {}, search: {}, page: {}, limit: {}",
                estatus, conjuntoId, search, page, limit);
        return ResponseEntity.ok(propietarioService.getPropietarios(estatus, conjuntoId, search, page, limit));
    }

    @Operation(summary = "Obtener propietario por ID compuesto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario encontrado"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{conjId}/{perId}")
    public ResponseEntity<PropietarioDTO> getPropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId
    ) {
        log.debug("GET /propietarios/{}/{}", conjId, perId);
        return ResponseEntity.ok(propietarioService.getPropietarioById(conjId, perId));
    }

    @Operation(summary = "Crear propietario", description = "Asigna una persona como propietaria de una propiedad en un conjunto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario creado exitosamente"),
            @ApiResponse(responseCode = "409", description = "El propietario ya existe en este conjunto"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<PropietarioDTO> createPropietario(
            @Valid @RequestBody CreatePropietarioRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /propietarios - conjId: {}, perId: {}", request.conjId, request.perId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        PropietarioDTO result = propietarioService.createPropietario(
                request.conjId, request.perId, request.propiedadId,
                request.fechaDesde, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Actualizar propietario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("/{conjId}/{perId}")
    public ResponseEntity<PropietarioDTO> updatePropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId,
            @Valid @RequestBody UpdatePropietarioRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /propietarios/{}/{}", conjId, perId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        PropietarioDTO result = propietarioService.updatePropietario(
                conjId, perId, request.propiedadId, request.fechaDesde,
                request.fechaHasta, request.estatus, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Inactivar propietario", description = "Realiza un soft-delete cambiando el estatus a 'I'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario inactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/{conjId}/{perId}")
    public ResponseEntity<Map<String, String>> deletePropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId,
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /propietarios/{}/{}", conjId, perId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        return ResponseEntity.ok(propietarioService.deletePropietario(conjId, perId, username, estacion));
    }

    // ==================== Inner Request DTOs ====================

    public record CreatePropietarioRequest(
            @NotNull Integer conjId,
            @NotNull Integer perId,
            @NotNull Integer propiedadId,
            @NotBlank String fechaDesde
    ) {}

    public record UpdatePropietarioRequest(
            Integer propiedadId,
            String fechaDesde,
            String fechaHasta,
            @Size(min = 1, max = 1) String estatus
    ) {}
}
