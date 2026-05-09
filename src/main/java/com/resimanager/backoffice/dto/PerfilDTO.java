package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String estatus;
    private Integer nivel;
    private OffsetDateTime fechaCreacion;
    private Long usuariosAsignados;
}
