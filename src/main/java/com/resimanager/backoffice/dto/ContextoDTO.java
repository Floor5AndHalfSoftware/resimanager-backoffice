package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.List;

/**
 * DTO que representa un contexto de trabajo del usuario
 * Incluye la combinación de Administradora/Conjunto y sus perfiles disponibles
 */
@Builder
public record ContextoDTO(
    String tipo,
    AdministradoraDTO administradora,
    ConjuntoDTO conjunto,
    List<PerfilDTO> perfilesDisponibles
) {}
