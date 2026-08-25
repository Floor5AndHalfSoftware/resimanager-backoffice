package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.PropiedadUseCase;
import com.resimanager.backoffice.domain.port.in.PropietarioUseCase;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.dto.PropietarioDTO;
import com.resimanager.backoffice.dto.PropietarioListResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/propietarios")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Propietarios", description = "Gestión de propietarios (asignación persona-propiedad en conjunto)")
public class PropietarioController {

    private final PropietarioUseCase propietarioUseCase;
    private final PropiedadUseCase propiedadUseCase;
    private final UsuarioUseCase usuarioUseCase;

    @Operation(summary = "Listar propietarios", description = "Obtiene la lista de propietarios con filtros opcionales")
    @GetMapping
    public ResponseEntity<PropietarioListResponse> listarPropietarios(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Filtrar por ID del conjunto") @RequestParam(required = false) Integer conjuntoId,
            @Parameter(description = "Búsqueda por persona") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") @Min(1) Integer limit
    ) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;
        ResultadoPaginado<Propietario> result =
                propietarioUseCase.obtenerPropietarios(estatusParam, conjuntoId, search, page, limit);

        List<PropietarioDTO> data = result.datos().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(PropietarioListResponse.builder()
                .data(data)
                .total(result.total())
                .page(result.pagina())
                .limit(result.limite())
                .build());
    }

    @Operation(summary = "Obtener propietario por ID compuesto")
    @GetMapping("/{conjId}/{perId}")
    public ResponseEntity<PropietarioDTO> getPropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId
    ) {
        Propietario propietario = propietarioUseCase.obtenerPropietario(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));
        return ResponseEntity.ok(toDTO(propietario));
    }

    @Operation(summary = "Crear propietario", description = "Asigna una persona como propietaria de una propiedad en un conjunto")
    @PostMapping
    public ResponseEntity<PropietarioDTO> createPropietario(
            @Valid @RequestBody CreatePropietarioRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Propietario propietario = propietarioUseCase.crearPropietario(request.conjId, request.perId,
                request.propiedadId, LocalDate.parse(request.fechaDesde), null,
                auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(toDTO(propietario));
    }

    @Operation(summary = "Actualizar propietario")
    @PutMapping("/{conjId}/{perId}")
    public ResponseEntity<PropietarioDTO> updatePropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId,
            @Valid @RequestBody UpdatePropietarioRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Propietario propietario = propietarioUseCase.actualizarPropietario(conjId, perId,
                request.propiedadId,
                request.fechaDesde != null ? LocalDate.parse(request.fechaDesde) : null,
                request.fechaHasta != null ? LocalDate.parse(request.fechaHasta) : null,
                estatus, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(toDTO(propietario));
    }

    @Operation(summary = "Inactivar propietario", description = "Realiza un soft-delete cambiando el estatus a 'I'")
    @DeleteMapping("/{conjId}/{perId}")
    public ResponseEntity<Map<String, String>> deletePropietario(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID de la persona") @PathVariable Integer perId,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        propietarioUseCase.inactivarPropietario(conjId, perId, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Propietario inactivado correctamente"));
    }

    private PropietarioDTO toDTO(Propietario p) {
        String nombrePersona = null;
        String apellidoPersona = null;
        String documentoPersona = null;
        String nombrePropiedad = null;

        Optional<com.resimanager.backoffice.domain.model.Persona> persona =
                usuarioUseCase.obtenerUsuario(p.getId().getPptPerid());
        if (persona.isPresent()) {
            nombrePersona = persona.get().getPerNombre();
            apellidoPersona = persona.get().getPerApellido();
            documentoPersona = persona.get().getPerDocIdent();
        }

        Optional<Propiedad> prop = propiedadUseCase.obtenerPropiedad(p.getPptID());
        if (prop.isPresent()) {
            nombrePropiedad = prop.get().getPpNumero();
        }

        return PropietarioDTO.builder()
                .conjId(p.getId().getPptConjid())
                .perId(p.getId().getPptPerid())
                .personaNombre(nombrePersona)
                .personaApellido(apellidoPersona)
                .personaDocumento(documentoPersona)
                .propiedadId(p.getPptID())
                .propiedadNombre(nombrePropiedad)
                .fechaDesde(p.getPptFchDesde() != null ? p.getPptFchDesde().toString() : null)
                .fechaHasta(p.getPptFchHasta() != null ? p.getPptFchHasta().toString() : null)
                .estatus(p.getPptSts())
                .build();
    }

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