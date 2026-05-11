package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadDTO {
    private Integer ppid;
    private Integer ppConjId;
    private String conjuntoNombre;
    private Integer ppCdpId;
    private String clasePropiedadNombre;
    private String ppNumero;
    private BigDecimal ppCantidad;
    private BigDecimal ppCoefParticipacion;
    private String estatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private List<PropiedadDTO> data;
        private Long total;
        private Integer page;
        private Integer limit;
    }
}
