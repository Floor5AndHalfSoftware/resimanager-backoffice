package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AsignarPerfilesRequest(
    @NotNull(message = "La lista de perfiles no puede ser nula")
    @NotEmpty(message = "Debe especificar al menos un perfil")
    List<Integer> perfiles
) {}
