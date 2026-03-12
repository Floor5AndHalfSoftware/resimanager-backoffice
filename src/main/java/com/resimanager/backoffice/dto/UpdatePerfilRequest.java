package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePerfilRequest {
    
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    private String nombre;
    
    @Size(max = 120, message = "La descripción no puede exceder 120 caracteres")
    private String descripcion;
    
    @Pattern(regexp = "^[AI]$", message = "El estatus debe ser 'A' (Activo) o 'I' (Inactivo)")
    private String estatus;
    
    @Min(value = 0, message = "El nivel mínimo es 0")
    @Max(value = 3, message = "El nivel máximo es 3")
    private Integer nivel;
}
