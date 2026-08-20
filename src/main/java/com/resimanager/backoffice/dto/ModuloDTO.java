package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ModuloDTO(
    Integer id,
    String nombre,
    String descripcion,
    Integer nivel
) {}
