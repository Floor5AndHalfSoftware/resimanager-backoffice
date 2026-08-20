package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PermisoDTO(
    @JsonProperty("modulo_id") Integer moduloId,
    @JsonProperty("modulo") String modulo,
    @JsonProperty("acciones") List<String> acciones
) {}
