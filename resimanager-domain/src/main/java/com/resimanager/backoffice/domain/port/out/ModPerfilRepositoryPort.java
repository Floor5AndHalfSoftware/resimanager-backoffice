package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.ModPerfil;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de la asignación módulo-perfil.
 */
public interface ModPerfilRepositoryPort {

    List<ModPerfil> listarActivosPorPerfilId(Integer perfilId);

    Optional<ModPerfil> buscarPorId(Integer perfilId, Integer moduloId);

    ModPerfil guardar(ModPerfil asignacion);

    void eliminar(Integer perfilId, Integer moduloId);
}