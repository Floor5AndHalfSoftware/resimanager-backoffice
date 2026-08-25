package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Casos de uso de gestión de propiedades.
 */
public interface PropiedadUseCase {

    ResultadoPaginado<Propiedad> obtenerPropiedades(Estatus estatus, Integer conjuntoId,
                                                    String busqueda, int pagina, int limite);

    Optional<Propiedad> obtenerPropiedad(Integer id);

    Propiedad crearPropiedad(Integer conjuntoId, Integer claseDePropiedadId, String numero,
                             BigDecimal cantidad, BigDecimal coeficiente,
                             String ejecutor, String estacion);

    Propiedad actualizarPropiedad(Integer id, Integer claseDePropiedadId, String numero,
                                  BigDecimal cantidad, BigDecimal coeficiente, Estatus estatus,
                                  String ejecutor, String estacion);

    void inactivarPropiedad(Integer id, String ejecutor, String estacion);
}