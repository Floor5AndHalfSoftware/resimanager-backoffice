package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropietarioDTO {
    private Integer conjId;
    private Integer perId;
    private String personaNombre;
    private String personaApellido;
    private String personaDocumento;
    private Integer propiedadId;
    private String propiedadNombre;
    private String fechaDesde;
    private String fechaHasta;
    private String estatus;
}
