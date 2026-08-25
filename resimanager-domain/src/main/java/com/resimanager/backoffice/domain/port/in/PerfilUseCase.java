package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Casos de uso de gestión de perfiles.
 */
public interface PerfilUseCase {

    ResultadoPaginado<Perfil> obtenerPerfiles(Estatus estatus, Integer nivel, String busqueda,
                                              int pagina, int limite);

    Optional<Perfil> obtenerPerfil(Integer id);

    Perfil crearPerfil(String nombre, String descripcion, Integer nivel, String ejecutor, String estacion);

    Perfil actualizarPerfil(Integer id, String nombre, String descripcion, Integer nivel,
                            Estatus estatus, String ejecutor, String estacion);

    void inactivarPerfil(Integer id, String ejecutor, String estacion);

    void asignarModulos(Integer perfilId, List<Integer> moduloIds, String ejecutor, String estacion);

    void revocarModulo(Integer perfilId, Integer moduloId, String ejecutor, String estacion);
}