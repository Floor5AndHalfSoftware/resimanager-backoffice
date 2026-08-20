package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PerfilDetalleDTO(
    Integer id,
    String nombre,
    String descripcion,
    String estatus,
    Integer nivel,
    List<ModuloDTO> modulos,
    List<PermisoDTO> permisos,
    Long usuariosAsignados,
    OffsetDateTime fechaCreacion
) {}
