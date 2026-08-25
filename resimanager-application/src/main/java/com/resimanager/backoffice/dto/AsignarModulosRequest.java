package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AsignarModulosRequest(
    @NotNull(message = "La lista de módulos no puede ser nula")
    @NotEmpty(message = "Debe especificar al menos un módulo")
    List<Integer> modulos
) {}
