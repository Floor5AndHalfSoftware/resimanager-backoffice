package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.Optional;

/**
 * Puerto de salida de persistencia de propiedades.
 */
public interface PropiedadRepositoryPort {

    Optional<Propiedad> buscarPorId(Integer id);

    Optional<Propiedad> buscarPorPpidYConjuntoId(Integer ppid, Integer conjuntoId);

    ResultadoPaginado<Propiedad> buscarConFiltros(Estatus estatus, Integer conjuntoId,
                                                  String busqueda, int pagina, int limite);

    Propiedad guardar(Propiedad propiedad);

    void eliminar(Integer id);

    long contarActivas();
}