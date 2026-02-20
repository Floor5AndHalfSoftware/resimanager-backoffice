package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para cambio de contexto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambioContextoRequest {
    
    @NotBlank(message = "El tipo de contexto es obligatorio")
    private String tipo; // "ADMINISTRADORA" o "CONJUNTO"
    
    @NotNull(message = "El ID de la entidad es obligatorio")
    private Integer entidadId; // ID de administradora o conjunto
    
    @NotNull(message = "El ID del perfil es obligatorio")
    private Integer perfilId; // ID del perfil a usar en este contexto
}
