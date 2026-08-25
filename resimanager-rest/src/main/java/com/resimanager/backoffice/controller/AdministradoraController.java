package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuarioConPerfiles;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.AdministradoraUseCase;
import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.dto.AdministradoraListResponse;
import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.dto.CreateAdministradoraRequest;
import com.resimanager.backoffice.dto.UpdateAdministradoraRequest;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.AdministradoraMapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/administradoras")
@RequiredArgsConstructor
@Validated
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administradoras", description = "Gestión de administradoras y sus usuarios")
public class AdministradoraController {

    private final AdministradoraUseCase administradoraUseCase;
    private final AdministradoraMapper administradoraMapper;

    @Operation(summary = "Listar administradoras", description = "Obtiene la lista de administradoras con filtros opcionales")
    @GetMapping
    public ResponseEntity<AdministradoraListResponse> listarAdministradoras(
            @Parameter(description = "Filtrar por estatus (A/I)") @RequestParam(required = false) String estatus,
            @Parameter(description = "Búsqueda por nombre o documento") @RequestParam(required = false) String search,
            @Parameter(description = "Número de página (inicia en 1)") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "Registros por página") @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;
        ResultadoPaginado<Administradora> result =
                administradoraUseCase.obtenerAdministradoras(estatusParam, search, page, limit);

        List<AdministradoraDTO> data = result.datos().stream().map(administradoraMapper::toDTO).toList();
        return ResponseEntity.ok(AdministradoraListResponse.builder()
                .data(data)
                .total(result.total())
                .page(result.pagina())
                .limit(result.limite())
                .build());
    }

    @Operation(summary = "Obtener administradora por ID", description = "Obtiene la información completa de una administradora.")
    @GetMapping("/{id}")
    public ResponseEntity<AdministradoraDTO> getAdministradoraById(
            @Parameter(description = "ID de la administradora") @PathVariable Integer id
    ) {
        Administradora adm = administradoraUseCase.obtenerAdministradora(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));
        return ResponseEntity.ok(administradoraMapper.toDTO(adm));
    }

    @Operation(summary = "Crear administradora", description = "Crea una nueva administradora")
    @PostMapping
    public ResponseEntity<AdministradoraDTO> createAdministradora(
            @Valid @RequestBody CreateAdministradoraRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Administradora adm = administradoraUseCase.crearAdministradora(request.documento(), request.nombre(),
                request.telefono(), request.email(), null, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(administradoraMapper.toDTO(adm));
    }

    @Operation(summary = "Actualizar administradora", description = "Actualiza los datos de una administradora existente")
    @PutMapping("/{id}")
    public ResponseEntity<AdministradoraDTO> updateAdministradora(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAdministradoraRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Estatus estatus = (request.estatus() != null && !request.estatus().isBlank())
                ? Estatus.desdeCodigo(request.estatus()) : null;
        Administradora adm = administradoraUseCase.actualizarAdministradora(id, request.documento(),
                request.nombre(), request.telefono(), request.email(), estatus,
                auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(administradoraMapper.toDTO(adm));
    }

    @Operation(summary = "Inactivar administradora", description = "Inactiva (soft-delete) una administradora")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAdministradora(
            @PathVariable Integer id,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        administradoraUseCase.inactivarAdministradora(id, auth.getName(), httpRequest.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message", "Administradora inactivada correctamente"));
    }

    @Operation(summary = "Listar usuarios de una administradora", description = "Obtiene todos los usuarios activos pertenecientes a la administradora, junto con sus perfiles.")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<ContextoUsuariosResponse> getUsuarios(
            @Parameter(description = "ID de la administradora") @PathVariable Integer id
    ) {
        UsuariosContexto ctx = administradoraUseCase.obtenerUsuariosAdministradora(id);

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

    @Operation(summary = "Asignar perfiles a un usuario en la administradora", description = "Asigna uno o más perfiles a un usuario dentro de la administradora.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfiles asignados exitosamente",
                    content = @Content(examples = @ExampleObject(
                            value = "{\"message\": \"Perfiles asignados correctamente\", \"perfiles_asignados\": 2, \"perfiles_reactivados\": 0}"))),
            @ApiResponse(responseCode = "404", description = "Administradora, usuario o perfil no encontrado")
    })
    @PostMapping("/{admId}/usuarios/{usuarioId}/perfiles")
    public ResponseEntity<Map<String, Object>> asignarPerfiles(
            @Parameter(description = "ID de la administradora") @PathVariable Integer admId,
            @Parameter(description = "ID del usuario (persona)") @PathVariable Integer usuarioId,
            @Valid @RequestBody AsignarPerfilesRequest request,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResultadoAsignacion resultado = administradoraUseCase.asignarPerfilesAUsuario(
                admId, usuarioId, request.perfiles(), auth.getName(), httpRequest.getRemoteAddr());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Perfiles asignados correctamente");
        result.put("perfiles_asignados", resultado.asignados());
        result.put("perfiles_reactivados", resultado.reactivados());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Remover perfil de un usuario en la administradora", description = "Inactiva la asignación de un perfil específico a un usuario.")
    @DeleteMapping("/{admId}/usuarios/{usuarioId}/perfiles/{perfilId}")
    public ResponseEntity<Map<String, String>> removerPerfil(
            @Parameter(description = "ID de la administradora") @PathVariable Integer admId,
            @Parameter(description = "ID del usuario (persona)") @PathVariable Integer usuarioId,
            @Parameter(description = "ID del perfil a remover") @PathVariable Integer perfilId,
            HttpServletRequest httpRequest
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        administradoraUseCase.removerPerfilDeUsuario(admId, usuarioId, perfilId,
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