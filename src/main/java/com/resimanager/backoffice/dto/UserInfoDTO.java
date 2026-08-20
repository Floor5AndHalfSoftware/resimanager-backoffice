package com.resimanager.backoffice.dto;

import lombok.Builder;

/**
 * DTO que representa los datos de usuario para incluir en JWT
 */
@Builder
public record UserInfoDTO(
    Integer id,
    String usuario,
    String nombre,
    String apellido,
    String email,
    String documento
) {}
