package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PermisoModulo;

import java.util.List;

/**
 * Puerto de salida de consulta de permisos de un perfil.
 */
public interface AccOpcPerfilRepositoryPort {

    List<PermisoModulo> listarPermisosPorPerfilId(Integer perfilId);

    boolean tienePermiso(Integer perfilId, String modulo, String accion);
}