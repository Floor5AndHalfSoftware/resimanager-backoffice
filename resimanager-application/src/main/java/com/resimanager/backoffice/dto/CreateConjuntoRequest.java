package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConjuntoRequest(
        @NotBlank @Size(max = 20) String documento,
        @NotBlank @Size(max = 80) String nombre,
        @NotBlank @Size(max = 15) String telefono,
        @NotBlank @Size(max = 80) String email,
        @NotNull Integer persContactoId
) {}
