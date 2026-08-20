package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PropiedadDTO(
    Integer ppid,
    Integer ppConjId,
    String conjuntoNombre,
    Integer ppCdpId,
    String clasePropiedadNombre,
    String ppNumero,
    BigDecimal ppCantidad,
    BigDecimal ppCoefParticipacion,
    String estatus
) {
    @Builder
    public record ListResponse(
        List<PropiedadDTO> data,
        Long total,
        Integer page,
        Integer limit
    ) {}
}
