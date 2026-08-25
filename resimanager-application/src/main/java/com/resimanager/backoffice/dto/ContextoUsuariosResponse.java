package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ContextoUsuariosResponse(
    Integer id,
    String nombre,
    List<UsuarioContextoDTO> data,
    Long total
) {
    @Builder
    public record UsuarioContextoDTO(
        PersonaSimpleDTO persona,
        List<PerfilSimpleDTO> perfiles
    ) {}

    @Builder
    public record PersonaSimpleDTO(
        Integer id,
        String documento,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String estatus
    ) {}

    @Builder
    public record PerfilSimpleDTO(
        Integer id,
        String nombre
    ) {}
}
