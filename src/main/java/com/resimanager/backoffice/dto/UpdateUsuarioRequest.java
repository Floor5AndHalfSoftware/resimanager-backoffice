package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequest {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String estatus;
}
