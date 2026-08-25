package com.resimanager.backoffice.dto;

import lombok.Builder;

@Builder
public record UsuarioDTO(
    Integer id,
    String documento,
    String nombre,
    String apellido,
    String email,
    String telefono,
    String usuario,
    String estatus
) {}
