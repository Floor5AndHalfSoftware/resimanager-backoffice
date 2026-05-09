package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a menu item in the hierarchical menu structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    private Integer id;
    private String nombre;
    private String tipo; // "O" = Opción (leaf), "M" = Menú (parent)
    private Integer itemPadre; // 0 if root level
    private Integer orden;
    private String controlador; // Controller path
    private String metodo; // HTTP method
    private String modulo; // Module name
    private String accion; // Action name
    private List<MenuItemDTO> hijos; // Child menu items
}
