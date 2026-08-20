package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PropietarioListResponse(
    List<PropietarioDTO> data,
    Long total,
    Integer page,
    Integer limit
) {}
