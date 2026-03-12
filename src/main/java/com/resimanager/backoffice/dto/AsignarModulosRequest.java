package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarModulosRequest {
    
    @NotNull(message = "La lista de módulos no puede ser nula")
    @NotEmpty(message = "Debe especificar al menos un módulo")
    private List<Integer> modulos;
}
