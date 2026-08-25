package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AdministradoraListResponse(
    List<AdministradoraDTO> data,
    Long total,
    Integer page,
    Integer limit
) {}
