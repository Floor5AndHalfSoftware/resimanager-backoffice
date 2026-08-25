package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de administradoras.
 */
public interface AdministradoraRepositoryPort {

    Optional<Administradora> buscarPorId(Integer id);

    ResultadoPaginado<Administradora> buscarConFiltros(Estatus estatus, String busqueda,
                                                       int pagina, int limite);

    List<Administradora> buscarActivasPorIds(List<Integer> ids);

    Administradora guardar(Administradora administradora);

    Integer siguienteId();

    long contarActivas();
}