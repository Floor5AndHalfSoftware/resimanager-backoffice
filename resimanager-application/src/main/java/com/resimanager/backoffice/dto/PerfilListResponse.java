package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PerfilListResponse(
    List<PerfilDTO> data,
    Long total,
    Integer page,
    Integer limit
) {}
