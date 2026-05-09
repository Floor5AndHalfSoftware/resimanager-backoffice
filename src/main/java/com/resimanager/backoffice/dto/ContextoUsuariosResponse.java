package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextoUsuariosResponse {

    private Integer id;
    private String nombre;
    private List<UsuarioContextoDTO> data;
    private Long total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioContextoDTO {
        private PersonaSimpleDTO persona;
        private List<PerfilSimpleDTO> perfiles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonaSimpleDTO {
        private Integer id;
        private String documento;
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String estatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerfilSimpleDTO {
        private Integer id;
        private String nombre;
    }
}
