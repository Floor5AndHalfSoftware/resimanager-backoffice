package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdministradoraRequest(
        @NotBlank @Size(max = 50) String documento,
        @NotBlank @Size(max = 250) String nombre,
        @NotBlank @Size(max = 15) String telefono,
        @NotBlank @Size(max = 250) String email
) {}
