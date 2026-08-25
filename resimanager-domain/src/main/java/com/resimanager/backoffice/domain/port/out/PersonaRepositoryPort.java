package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de personas (usuarios).
 */
public interface PersonaRepositoryPort {

    Optional<Persona> buscarPorId(Integer id);

    Optional<Persona> buscarPorUsuario(String usuario);

    Optional<Persona> buscarPorUsuarioOEmail(String usuario, String email);

    ResultadoPaginado<Persona> buscarConFiltros(Estatus estatus, String busqueda, int pagina, int limite);

    List<Persona> buscarActivasPorIds(List<Integer> ids);

    Persona guardar(Persona persona);
}