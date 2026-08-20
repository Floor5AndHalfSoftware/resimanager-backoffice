package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record LoginRequestJson(
    @NotBlank(message = "El usuario es obligatorio")
    String username,
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
