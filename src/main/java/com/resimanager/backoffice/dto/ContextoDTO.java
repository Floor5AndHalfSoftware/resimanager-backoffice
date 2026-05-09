package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa un contexto de trabajo del usuario
 * Incluye la combinación de Administradora/Conjunto y sus perfiles disponibles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextoDTO {
    private String tipo; // "ADMINISTRADORA" o "CONJUNTO"
    private AdministradoraDTO administradora;
    private ConjuntoDTO conjunto;
    private List<PerfilDTO> perfilesDisponibles;
}
