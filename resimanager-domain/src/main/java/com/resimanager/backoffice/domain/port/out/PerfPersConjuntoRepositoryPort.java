package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PerfPersConjunto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de persistencia del perfil de una persona en un conjunto.
 */
public interface PerfPersConjuntoRepositoryPort {

    List<PerfPersConjunto> listarActivasPorPersonaId(Integer personaId);

    List<PerfPersConjunto> listarActivasPorConjuntoIdYPersonaId(Integer conjuntoId, Integer personaId);

    Optional<PerfPersConjunto> buscarPorId(Integer conjuntoId, Integer personaId, Integer perfilId);

    PerfPersConjunto guardar(PerfPersConjunto relacion);

    void eliminar(Integer personaId, Integer conjuntoId, Integer perfilId);

    Integer siguienteId();
}