package com.resimanager.backoffice.domain.model;

import java.util.List;

/**
 * Detalle de un perfil: datos + módulos asignados + permisos + usuarios asignados.
 */
public record PerfilDetalle(
        Perfil perfil,
        List<Modulo> modulos,
        List<PermisoModulo> permisos,
        long usuariosAsignados
) {}