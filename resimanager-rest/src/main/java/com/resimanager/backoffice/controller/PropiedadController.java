package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.ConjuntoUseCase;
import com.resimanager.backoffice.domain.port.in.PropiedadUseCase;
import com.resimanager.backoffice.domain.port.out.ClaseDePropiedadRepositoryPort;
import com.resimanager.backoffice.dto.PropiedadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
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

    private final PropiedadUseCase propiedadUseCase;
    private final ConjuntoUseCase conjuntoUseCase;
    private final ClaseDePropiedadRepositoryPort claseDePropiedadRepositoryPort;

    @Operation(summary = "Listar clases de propiedad", description = "Para dropdown en formularios")
    @GetMapping("/clases")
    public ResponseEntity<List<com.resimanager.backoffice.domain.model.ClaseDePropiedadOpcion>> listarClases() {
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
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;
        ResultadoPaginado<Propiedad> result =
                propiedadUseCase.obtenerPropiedades(estatusParam, conjuntoId, search, page, limit);

        List<PropiedadDTO> data = result.datos().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(new PropiedadDTO.ListResponse(data, result.total(), result.pagina(), result.limite()));
    }

    @Operation(summary = "Obtener propiedad por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadDTO> getPropiedad(@PathVariable Integer id) {
        Propiedad prop = propiedadUseCase.obtenerPropiedad(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));
        return ResponseEntity.ok(toDTO(prop));
    }

    @Operation(summary = "Crear propiedad")
    @PostMapping
    public ResponseEntity<PropiedadDTO> createPropiedad(
            @Valid @RequestBody CreatePropiedadRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Propiedad prop = propiedadUseCase.crearPropiedad(request.conjId, request.cdpId, request.numero,
                request.cantidad, request.coefParticipacion, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(toDTO(prop));
    }

    @Operation(summary = "Actualizar propiedad")
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadDTO> updatePropiedad(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePropiedadRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Propiedad prop = propiedadUseCase.actualizarPropiedad(id, request.cdpId, request.numero,
                request.cantidad, request.coefParticipacion, estatus, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(toDTO(prop));
    }

    @Operation(summary = "Inactivar propiedad")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePropiedad(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        propiedadUseCase.inactivarPropiedad(id, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Propiedad inactivada correctamente"));
    }

    private PropiedadDTO toDTO(Propiedad p) {
        String conjNombre = null;
        java.util.Optional<Conjunto> conj = conjuntoUseCase.obtenerConjunto(p.getPpConjId());
        if (conj.isPresent()) {
            conjNombre = conj.get().getConjNombre();
        }

        return PropiedadDTO.builder()
                .ppid(p.getPpid())
                .ppConjId(p.getPpConjId())
                .conjuntoNombre(conjNombre)
                .ppCdpId(p.getPpCdpId())
                .ppNumero(p.getPpNumero())
                .ppCantidad(p.getPpCantidad())
                .ppCoefParticipacion(p.getPpCoefParticipacion())
                .estatus(p.getPpSts())
                .build();
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