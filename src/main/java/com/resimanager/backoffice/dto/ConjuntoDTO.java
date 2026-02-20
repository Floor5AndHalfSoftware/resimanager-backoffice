package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConjuntoDTO {
    private Integer id;
    private String nombre;
    private String documento;
    private String direccion;
    private String tipo;
}
