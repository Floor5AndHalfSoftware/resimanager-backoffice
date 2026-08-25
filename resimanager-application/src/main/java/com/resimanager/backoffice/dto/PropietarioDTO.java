package com.resimanager.backoffice.dto;

import lombok.Builder;

@Builder
public record PropietarioDTO(
    Integer conjId,
    Integer perId,
    String personaNombre,
    String personaApellido,
    String personaDocumento,
    Integer propiedadId,
    String propiedadNombre,
    String fechaDesde,
    String fechaHasta,
    String estatus
) {}
