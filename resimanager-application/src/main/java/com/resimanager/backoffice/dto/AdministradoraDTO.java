package com.resimanager.backoffice.dto;

import lombok.Builder;

@Builder
public record AdministradoraDTO(
    Integer id,
    String nombre,
    String documento,
    String email,
    String telefono,
    String estatus
) {}
