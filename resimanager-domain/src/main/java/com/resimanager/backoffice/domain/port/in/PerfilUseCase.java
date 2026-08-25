package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PerfilDetalle;
import com.resimanager.backoffice.domain.model.PerfilResumen;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Casos de uso de gestión de perfiles.
 */
public interface PerfilUseCase {

    ResultadoPaginado<PerfilResumen> obtenerPerfiles(Estatus estatus, Integer nivel, String busqueda,
                                                     int pagina, int limite);

    Optional<Perfil> obtenerPerfil(Integer id);

    PerfilDetalle obtenerDetallePerfil(Integer id);

    Perfil crearPerfil(String nombre, String descripcion, Integer nivel, String ejecutor, String estacion);

    Perfil actualizarPerfil(Integer id, String nombre, String descripcion, Integer nivel,
                            Estatus estatus, String ejecutor, String estacion);

    void inactivarPerfil(Integer id, String ejecutor, String estacion);

    int asignarModulos(Integer perfilId, List<Integer> moduloIds, String ejecutor, String estacion);

    void revocarModulo(Integer perfilId, Integer moduloId, String ejecutor, String estacion);
}