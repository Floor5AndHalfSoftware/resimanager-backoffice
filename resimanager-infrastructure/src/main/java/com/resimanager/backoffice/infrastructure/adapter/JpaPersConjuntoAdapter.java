package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PersConjunto;
import com.resimanager.backoffice.domain.port.out.PersConjuntoRepositoryPort;
import com.resimanager.backoffice.persistance.repository.PersConjuntoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaPersConjuntoAdapter implements PersConjuntoRepositoryPort {

    private final PersConjuntoRepository repository;

    @Override
    public List<PersConjunto> listarActivasPorPersonaId(Integer personaId) {
        return repository.findActiveByPersonaId(personaId);
    }

    @Override
    public List<PersConjunto> listarActivasPorConjuntoId(Integer conjuntoId) {
        return repository.findActiveByConjId(conjuntoId);
    }

    @Override
    public PersConjunto guardar(PersConjunto relacion) {
        return repository.save(relacion);
    }
}