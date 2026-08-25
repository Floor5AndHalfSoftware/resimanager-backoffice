package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PersAdministradora;

import java.util.List;

/**
 * Puerto de salida de persistencia de la relación persona-administradora.
 */
public interface PersAdministradoraRepositoryPort {

    List<PersAdministradora> listarActivasPorPersonaId(Integer personaId);

    List<PersAdministradora> listarActivasPorAdministradoraId(Integer administradoraId);

    PersAdministradora guardar(PersAdministradora relacion);
}