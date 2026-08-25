package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuarioConPerfiles;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.ConjuntoUseCase;
import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ConjuntoListResponse;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.dto.CreateConjuntoRequest;
import com.resimanager.backoffice.dto.UpdateConjuntoRequest;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.ConjuntoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/conjuntos")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conjuntos", description = "Gestión de conjuntos y sus usuarios")
public class ConjuntoController {

    private final ConjuntoUseCase conjuntoUseCase;
    private final ConjuntoMapper conjuntoMapper;

    @Operation(summary = "Listar conjuntos", description = "Obtiene la lista de conjuntos con filtros opcionales")
    @GetMapping
    public ResponseEntity<ConjuntoListResponse> listarConjuntos(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Búsqueda por nombre o documento") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;
        ResultadoPaginado<Conjunto> result = conjuntoUseCase.obtenerConjuntos(estatusParam, search, page, limit);

        List<ConjuntoDTO> data = result.datos().stream().map(conjuntoMapper::toDTO).toList();
        return ResponseEntity.ok(ConjuntoListResponse.builder()
                .data(data)
                .total(result.total())
                .page(result.pagina())
                .limit(result.limite())
                .build());
    }

    @Operation(summary = "Obtener conjunto por ID", description = "Obtiene la información completa de un conjunto.")
    @GetMapping("/{id}")
    public ResponseEntity<ConjuntoDTO> getConjuntoById(
            @Parameter(description = "ID del conjunto") @PathVariable Integer id
    ) {
        Conjunto conj = conjuntoUseCase.obtenerConjunto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));
        return ResponseEntity.ok(conjuntoMapper.toDTO(conj));
    }

    @Operation(summary = "Crear conjunto", description = "Crea un nuevo conjunto")
    @PostMapping
    public ResponseEntity<ConjuntoDTO> createConjunto(
            @Valid @RequestBody CreateConjuntoRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Conjunto conj = conjuntoUseCase.crearConjunto(request.documento(), request.nombre(),
                request.telefono(), request.email(), request.persContactoId(),
                auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(conjuntoMapper.toDTO(conj));
    }

    @Operation(summary = "Actualizar conjunto", description = "Actualiza los datos de un conjunto existente")
    @PutMapping("/{id}")
    public ResponseEntity<ConjuntoDTO> updateConjunto(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateConjuntoRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Conjunto conj = conjuntoUseCase.actualizarConjunto(id, request.documento(), request.nombre(),
                request.telefono(), request.email(), request.persContactoId(), estatus,
                auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(conjuntoMapper.toDTO(conj));
    }

    @Operation(summary = "Inactivar conjunto", description = "Inactiva (soft-delete) un conjunto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteConjunto(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        conjuntoUseCase.inactivarConjunto(id, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Conjunto inactivado correctamente"));
    }

    @Operation(summary = "Listar usuarios de un conjunto", description = "Obtiene todos los usuarios activos pertenecientes al conjunto, junto con sus perfiles.")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<ContextoUsuariosResponse> getUsuarios(
            @Parameter(description = "ID del conjunto") @PathVariable Integer id
    ) {
        UsuariosContexto ctx = conjuntoUseCase.obtenerUsuariosConjunto(id);

        List<ContextoUsuariosResponse.UsuarioContextoDTO> usuarios = ctx.usuarios().stream()
                .map(this::toUsuarioContexto)
                .toList();

        return ResponseEntity.ok(ContextoUsuariosResponse.builder()
                .id(ctx.entidadId())
                .nombre(ctx.entidadNombre())
                .data(usuarios)
                .total((long) usuarios.size())
                .build());
    }

    @Operation(summary = "Asignar perfiles a un usuario en el conjunto", description = "Asigna uno o más perfiles a un usuario dentro del conjunto.")
    @PostMapping("/{conjId}/usuarios/{usuarioId}/perfiles")
    public ResponseEntity<Map<String, Object>> asignarPerfiles(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID del usuario (persona)") @PathVariable Integer usuarioId,
            @Valid @RequestBody AsignarPerfilesRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResultadoAsignacion resultado = conjuntoUseCase.asignarPerfilesAUsuario(
                conjId, usuarioId, request.perfiles(), auth.getName(), httpRequest.getRemoteAddr());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Perfiles asignados correctamente");
        result.put("perfiles_asignados", resultado.asignados());
        result.put("perfiles_reactivados", resultado.reactivados());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Remover perfil de un usuario en el conjunto", description = "Inactiva la asignación de un perfil específico a un usuario.")
    @DeleteMapping("/{conjId}/usuarios/{usuarioId}/perfiles/{perfilId}")
    public ResponseEntity<Map<String, String>> removerPerfil(
            @Parameter(description = "ID del conjunto") @PathVariable Integer conjId,
            @Parameter(description = "ID del usuario (persona)") @PathVariable Integer usuarioId,
            @Parameter(description = "ID del perfil a remover") @PathVariable Integer perfilId,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        conjuntoUseCase.removerPerfilDeUsuario(conjId, usuarioId, perfilId,
                auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Perfil removido correctamente"));
    }

    private ContextoUsuariosResponse.UsuarioContextoDTO toUsuarioContexto(UsuarioConPerfiles uc) {
        ContextoUsuariosResponse.PersonaSimpleDTO personaDTO = ContextoUsuariosResponse.PersonaSimpleDTO.builder()
                .id(uc.persona().getId())
                .documento(uc.persona().getPerDocIdent())
                .nombre(uc.persona().getPerNombre())
                .apellido(uc.persona().getPerApellido())
                .email(uc.persona().getPerEMail())
                .telefono(uc.persona().getPerTlfCel())
                .estatus(uc.persona().getPerSts())
                .build();

        List<ContextoUsuariosResponse.PerfilSimpleDTO> perfilesDTO = uc.perfiles().stream()
                .map(p -> ContextoUsuariosResponse.PerfilSimpleDTO.builder().id(p.id()).nombre(p.nombre()).build())
                .toList();

        return ContextoUsuariosResponse.UsuarioContextoDTO.builder()
                .persona(personaDTO)
                .perfiles(perfilesDTO)
                .build();
    }
}