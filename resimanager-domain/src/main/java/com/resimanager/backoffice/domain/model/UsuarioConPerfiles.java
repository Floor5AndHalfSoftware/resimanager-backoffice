package com.resimanager.backoffice.domain.model;

import java.util.List;

/**
 * Usuario con sus perfiles dentro de un contexto.
 */
public record UsuarioConPerfiles(Persona persona, List<PerfilSimple> perfiles) {}