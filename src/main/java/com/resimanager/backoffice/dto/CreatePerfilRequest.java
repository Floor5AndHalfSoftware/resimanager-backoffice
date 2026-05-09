package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePerfilRequest {
    
    @NotBlank(message = "El nombre del perfil es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    private String nombre;
    
    @Size(max = 120, message = "La descripción no puede exceder 120 caracteres")
    private String descripcion;
    
    @NotNull(message = "El nivel es obligatorio")
    @Min(value = 0, message = "El nivel mínimo es 0")
    @Max(value = 4, message = "El nivel máximo es 4")
    private Integer nivel;
}
