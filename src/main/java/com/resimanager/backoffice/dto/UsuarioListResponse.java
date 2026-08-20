package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioListResponse(
    List<UsuarioDTO> data,
    Long total,
    Integer page,
    Integer limit
) {}
