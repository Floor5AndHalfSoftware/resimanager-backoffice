package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

/**
 * DTO representing a menu item in the hierarchical menu structure
 */
@Builder
public record MenuItemDTO(
    Integer id,
    String nombre,
    String tipo,
    Integer itemPadre,
    Integer orden,
    String controlador,
    String metodo,
    String modulo,
    String accion,
    List<MenuItemDTO> hijos
) {}
