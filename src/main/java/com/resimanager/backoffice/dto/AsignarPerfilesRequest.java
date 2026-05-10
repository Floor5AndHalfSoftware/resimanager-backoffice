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
public class AsignarPerfilesRequest {

    @NotNull(message = "La lista de perfiles no puede ser nula")
    @NotEmpty(message = "Debe especificar al menos un perfil")
    private List<Integer> perfiles;
}
