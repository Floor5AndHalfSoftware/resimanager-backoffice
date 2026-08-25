package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.Optional;

/**
 * Puerto de salida de persistencia de propietarios.
 */
public interface PropietarioRepositoryPort {

    Optional<Propietario> buscarPorId(Integer conjuntoId, Integer personaId);

    boolean existePorId(Integer conjuntoId, Integer personaId);

    ResultadoPaginado<Propietario> buscarConFiltros(Estatus estatus, Integer conjuntoId,
                                                    String busqueda, int pagina, int limite);

    Propietario guardar(Propietario propietario);

    void eliminar(Integer conjuntoId, Integer personaId);

    long contarActivas();
}