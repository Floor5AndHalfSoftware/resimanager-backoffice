package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PerfPersAdministradora;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia del perfil de una persona en una administradora.
 */
public interface PerfPersAdministradoraRepositoryPort {

    List<PerfPersAdministradora> listarActivasPorPersonaId(Integer personaId);

    List<PerfPersAdministradora> listarActivasPorAdministradoraIdYPersonaId(Integer administradoraId, Integer personaId);

    Optional<PerfPersAdministradora> buscarPorId(Integer administradoraId, Integer personaId, Integer perfilId);

    PerfPersAdministradora guardar(PerfPersAdministradora relacion);

    void eliminar(Integer personaId, Integer administradoraId, Integer perfilId);

    Integer siguienteId();
}