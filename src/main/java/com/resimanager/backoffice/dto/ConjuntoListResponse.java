package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ConjuntoListResponse(
    List<ConjuntoDTO> data,
    Long total,
    Integer page,
    Integer limit
) {}
