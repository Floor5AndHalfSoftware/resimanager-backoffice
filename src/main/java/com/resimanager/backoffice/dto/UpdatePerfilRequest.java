package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePerfilRequest(
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    String nombre,
    @Size(max = 120, message = "La descripción no puede exceder 120 caracteres")
    String descripcion,
    @Pattern(regexp = "^[AI]$", message = "El estatus debe ser 'A' (Activo) o 'I' (Inactivo)")
    String estatus,
    @Min(value = 0, message = "El nivel mínimo es 0")
    @Max(value = 4, message = "El nivel máximo es 4")
    Integer nivel
) {}
