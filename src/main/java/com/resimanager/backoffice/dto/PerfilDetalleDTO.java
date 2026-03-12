package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilDetalleDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String estatus;
    private Integer nivel;
    private List<ModuloDTO> modulos;
    private Long usuariosAsignados;
    private OffsetDateTime fechaCreacion;
}
