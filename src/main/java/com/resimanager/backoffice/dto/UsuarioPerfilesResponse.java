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
public class UsuarioPerfilesResponse {

    private UsuarioDTO persona;
    private List<ContextoPerfilDTO> contextos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextoPerfilDTO {
        private String tipo; // "ADMINISTRADORA" | "CONJUNTO"
        private EntidadDTO entidad;
        private List<PerfilSimpleDTO> perfiles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntidadDTO {
        private Integer id;
        private String nombre;
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
