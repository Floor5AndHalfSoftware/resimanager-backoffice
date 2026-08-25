package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.PropiedadDTO;
import com.resimanager.backoffice.service.PropiedadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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

import java.math.BigDecimal;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/propiedades")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Propiedades", description = "Gestión de propiedades/unidades en conjuntos")
public class PropiedadController {

    private final PropiedadService propiedadService;
    private final com.resimanager.backoffice.domain.port.out.ClaseDePropiedadRepositoryPort claseDePropiedadRepositoryPort;

    @Operation(summary = "Listar clases de propiedad", description = "Para dropdown en formularios")
    @GetMapping("/clases")
    public ResponseEntity<java.util.List<com.resimanager.backoffice.domain.model.ClaseDePropiedadOpcion>> listarClases() {
        return ResponseEntity.ok(claseDePropiedadRepositoryPort.listarActivas());
    }

    @Operation(summary = "Listar propiedades", description = "Obtiene la lista de propiedades con filtros opcionales")
    @GetMapping
    public ResponseEntity<PropiedadDTO.ListResponse> listarPropiedades(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Filtrar por ID del conjunto") @RequestParam(required = false) Integer conjuntoId,
            @Parameter(description = "Búsqueda por número de propiedad") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") @Min(1) Integer limit
    ) {
        log.debug("GET /propiedades - estatus: {}, conjuntoId: {}, search: {}, page: {}, limit: {}",
                estatus, conjuntoId, search, page, limit);
        return ResponseEntity.ok(propiedadService.getPropiedades(estatus, conjuntoId, search, page, limit));
    }

    @Operation(summary = "Obtener propiedad por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propiedad encontrada"),
            @ApiResponse(responseCode = "404", description = "Propiedad no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadDTO> getPropiedad(@PathVariable Integer id) {
        log.debug("GET /propiedades/{}", id);
        return ResponseEntity.ok(propiedadService.getPropiedadById(id));
    }

    @Operation(summary = "Crear propiedad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propiedad creada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<PropiedadDTO> createPropiedad(
            @Valid @RequestBody CreatePropiedadRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /propiedades - conjId: {}, numero: {}", request.conjId, request.numero);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        PropiedadDTO result = propiedadService.createPropiedad(
                request.conjId, request.cdpId, request.numero,
                request.cantidad, request.coefParticipacion, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Actualizar propiedad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propiedad actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Propiedad no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadDTO> updatePropiedad(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePropiedadRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("PUT /propiedades/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        PropiedadDTO result = propiedadService.updatePropiedad(
                id, request.cdpId, request.numero, request.cantidad,
                request.coefParticipacion, request.estatus, username, estacion);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Inactivar propiedad")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePropiedad(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        log.info("DELETE /propiedades/{}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String estacion = httpRequest.getRemoteAddr();

        return ResponseEntity.ok(propiedadService.deletePropiedad(id, username, estacion));
    }

    public record CreatePropiedadRequest(
            @NotNull Integer conjId,
            @NotNull Integer cdpId,
            @NotBlank @Size(max = 20) String numero,
            @NotNull @DecimalMin("0") BigDecimal cantidad,
            @NotNull @DecimalMin("0") @Digits(integer = 10, fraction = 8) BigDecimal coefParticipacion
    ) {}

    public record UpdatePropiedadRequest(
            Integer cdpId,
            String numero,
            BigDecimal cantidad,
            BigDecimal coefParticipacion,
            @Size(min = 1, max = 1) String estatus
    ) {}
}
