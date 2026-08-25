package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Size;

public record UpdateAdministradoraRequest(
        @Size(max = 50) String documento,
        @Size(max = 250) String nombre,
        @Size(max = 15) String telefono,
        @Size(max = 250) String email,
        @Size(min = 1, max = 1) String estatus
) {}
