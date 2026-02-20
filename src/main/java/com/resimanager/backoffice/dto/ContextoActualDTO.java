package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa el contexto activo del usuario
 * Se devuelve después de cambiar de contexto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextoActualDTO {
    private String tipo; // "ADMINISTRADORA" o "CONJUNTO"
    private Integer entidadId; // ID de administradora o conjunto
    private String entidadNombre; // Nombre de la entidad
    private Integer perfilId;
    private String perfilNombre;
    private String perfilDescripcion;
}
