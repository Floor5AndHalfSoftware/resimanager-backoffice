package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.persistance.entity.Modulo;
import com.resimanager.backoffice.persistance.repository.ModuloRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/modulos")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Módulos", description = "Gestión de módulos del sistema")
public class ModuloController {

    private final ModuloRepository moduloRepository;

    @Operation(
            summary = "Listar módulos",
            description = """
                    Obtiene una lista de módulos del sistema.
                    
                    Por defecto, devuelve solo módulos activos.
                    
                    Puede filtrar por:
                    - **nivel**: 0 (Super Admin), 1 (Admin General), 2 (Admin Conjunto), 3 (Propietario), 4 (Residente)
                    
                    Los módulos se ordenan alfabéticamente por nombre.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de módulos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = ModuloDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado")
    })
    @GetMapping
    public ResponseEntity<List<ModuloDTO>> listarModulos(
            @Parameter(description = "Filtrar por nivel jerárquico (0-4)")
            @RequestParam(required = false) Integer nivel
    ) {
        log.debug("GET /modulos - nivel: {}", nivel);
        
        List<Modulo> modulos;
        
        if (nivel != null) {
            // Filtrar por nivel y solo activos
            modulos = moduloRepository.findByModNivelAndModSts(nivel, "A");
            log.debug("Encontrados {} módulos activos de nivel {}", modulos.size(), nivel);
        } else {
            // Obtener todos los módulos activos
            modulos = moduloRepository.findByModSts("A");
            log.debug("Encontrados {} módulos activos", modulos.size());
        }
        
        // Convertir a DTOs
        List<ModuloDTO> dtos = modulos.stream()
                .map(this::toDTO)
                .sorted((m1, m2) -> m1.getNombre().compareTo(m2.getNombre()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Convert Modulo entity to ModuloDTO
     * @param modulo Modulo entity
     * @return ModuloDTO
     */
    private ModuloDTO toDTO(Modulo modulo) {
        return ModuloDTO.builder()
                .id(modulo.getModId())
                .nombre(modulo.getModNombre())
                .descripcion(modulo.getModDescrip())
                .nivel(modulo.getModNivel())
                .build();
    }
}
