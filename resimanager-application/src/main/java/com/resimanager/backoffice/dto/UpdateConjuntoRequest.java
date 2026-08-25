package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Size;

public record UpdateConjuntoRequest(
        @Size(max = 20) String documento,
        @Size(max = 80) String nombre,
        @Size(max = 15) String telefono,
        @Size(max = 80) String email,
        Integer persContactoId,
        @Size(min = 1, max = 1) String estatus
) {}
