package com.resimanager.backoffice.domain.model;

/**
 * Perfil con el número de usuarios asignados (para listados).
 */
public record PerfilResumen(Perfil perfil, long usuariosAsignados) {}