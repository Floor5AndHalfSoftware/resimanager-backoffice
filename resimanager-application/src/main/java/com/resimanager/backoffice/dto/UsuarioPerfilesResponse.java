package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioPerfilesResponse(
    UsuarioDTO persona,
    List<ContextoPerfilDTO> contextos
) {
    @Builder
    public record ContextoPerfilDTO(
        String tipo,
        EntidadDTO entidad,
        List<PerfilSimpleDTO> perfiles
    ) {}

    @Builder
    public record EntidadDTO(
        Integer id,
        String nombre
    ) {}

    @Builder
    public record PerfilSimpleDTO(
        Integer id,
        String nombre
    ) {}
}
