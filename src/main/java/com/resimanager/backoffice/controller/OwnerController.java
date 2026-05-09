package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.OwnerRequestDto;
import com.resimanager.backoffice.dto.OwnerResponseDto;
import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.persistance.entity.Owner;
import com.resimanager.backoffice.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Propietarios", description = "CRUD de propietarios - API legacy (v1 anterior)")
public class OwnerController {

    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Operation(summary = "Listar propietarios", description = "Devuelve todos los propietarios paginados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de propietarios"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<OwnerResponseDto>> getAllOwners(
            @Parameter(description = "Número de página (0-based)", example = "0") @RequestParam int page,
            @Parameter(description = "Tamaño de página", example = "10") @RequestParam int size) {
        if (!isValidPagination(page, size)) {
            throw new ServiceException("Invalid pagination parameters", HttpStatus.BAD_REQUEST.value());
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<OwnerResponseDto> owners = ownerService.getAllOwners(pageable);
        return new ResponseEntity<>(owners, HttpStatus.OK);
    }

    private static boolean isValidPagination(int page, int size) {
        return page >= 0 && size > 0;
    }

    @Operation(summary = "Buscar propietario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario encontrado"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> findById(@Parameter(description = "ID del propietario") @PathVariable Long id) {
        try {
            OwnerResponseDto owner = ownerService.getOwnerById(id);
            return new ResponseEntity<>(owner, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Crear propietario")
    @ApiResponse(responseCode = "201", description = "Propietario creado correctamente")
    @PostMapping
    public ResponseEntity<OwnerResponseDto> createOwner(@RequestBody OwnerRequestDto owner) {
        OwnerResponseDto createdOwner = ownerService.createOwner(owner);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar propietario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Propietario actualizado"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponseDto> updateOwner(@Parameter(description = "ID del propietario") @PathVariable Long id, @RequestBody Owner ownerDetails) {
        Optional<OwnerResponseDto> updatedOwner = ownerService.updateOwner(id, ownerDetails);
        return updatedOwner.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Eliminar propietario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Propietario eliminado"),
            @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@Parameter(description = "ID del propietario") @PathVariable Long id) {
        ownerService.deleteOwner(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
