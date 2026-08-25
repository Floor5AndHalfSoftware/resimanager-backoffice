package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PersConjunto;

import java.util.List;

/**
 * Puerto de salida de persistencia de la relación persona-conjunto.
 */
public interface PersConjuntoRepositoryPort {

    List<PersConjunto> listarActivasPorPersonaId(Integer personaId);

    List<PersConjunto> listarActivasPorConjuntoId(Integer conjuntoId);

    PersConjunto guardar(PersConjunto relacion);
}