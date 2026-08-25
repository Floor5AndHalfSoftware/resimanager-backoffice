package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Casos de uso de gestión de propietarios.
 */
public interface PropietarioUseCase {

    ResultadoPaginado<Propietario> obtenerPropietarios(Estatus estatus, Integer conjuntoId,
                                                       String busqueda, int pagina, int limite);

    Optional<Propietario> obtenerPropietario(Integer conjuntoId, Integer personaId);

    Propietario crearPropietario(Integer conjuntoId, Integer personaId, Integer propiedadId,
                                 LocalDate fchDesde, LocalDate fchHasta, String ejecutor, String estacion);

    Propietario actualizarPropietario(Integer conjuntoId, Integer personaId, Integer propiedadId,
                                      LocalDate fchDesde, LocalDate fchHasta, Estatus estatus,
                                      String ejecutor, String estacion);

    void inactivarPropietario(Integer conjuntoId, Integer personaId, String ejecutor, String estacion);
}