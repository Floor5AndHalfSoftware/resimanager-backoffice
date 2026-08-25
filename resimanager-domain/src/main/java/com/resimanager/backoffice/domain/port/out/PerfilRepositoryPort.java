package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de perfiles.
 */
public interface PerfilRepositoryPort {

    Optional<Perfil> buscarPorId(Integer id);

    ResultadoPaginado<Perfil> buscarConFiltros(Estatus estatus, Integer nivel, String busqueda,
                                               int pagina, int limite);

    List<Perfil> buscarActivosPorIds(List<Integer> ids);

    Perfil guardar(Perfil perfil);

    boolean existePorNombre(String nombre);

    boolean existePorNombreYDistintoId(String nombre, Integer id);

    long contarUsuariosAsignados(Integer perfilId);
}