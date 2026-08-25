package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record PerfilDTO(
    Integer id,
    String nombre,
    String descripcion,
    String estatus,
    Integer nivel,
    OffsetDateTime fechaCreacion,
    Long usuariosAsignados
) {}
