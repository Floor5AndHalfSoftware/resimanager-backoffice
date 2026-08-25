package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de conjuntos.
 */
public interface ConjuntoRepositoryPort {

    Optional<Conjunto> buscarPorId(Integer id);

    ResultadoPaginado<Conjunto> buscarConFiltros(Estatus estatus, String busqueda, int pagina, int limite);

    List<Conjunto> buscarActivosPorIds(List<Integer> ids);

    Conjunto guardar(Conjunto conjunto);

    long contarActivas();
}