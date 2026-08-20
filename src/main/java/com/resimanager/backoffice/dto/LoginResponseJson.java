package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record LoginResponseJson(
    String type,
    String token,
    UserInfoDTO usuario,
    List<ContextoDTO> contextosDisponibles
) {}
