package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePerfilRequest(
    @NotBlank(message = "El nombre del perfil es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    String nombre,
    @Size(max = 120, message = "La descripción no puede exceder 120 caracteres")
    String descripcion,
    @NotNull(message = "El nivel es obligatorio")
    @Min(value = 0, message = "El nivel mínimo es 0")
    @Max(value = 4, message = "El nivel máximo es 4")
    Integer nivel
) {}
