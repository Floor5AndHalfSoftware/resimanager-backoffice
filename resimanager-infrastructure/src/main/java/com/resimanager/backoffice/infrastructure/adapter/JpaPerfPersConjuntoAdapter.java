package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PerfPersConjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjuntoId;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.persistance.repository.PerfPersConjuntoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPerfPersConjuntoAdapter implements PerfPersConjuntoRepositoryPort {

    private final PerfPersConjuntoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PerfPersConjunto> listarActivasPorPersonaId(Integer personaId) {
        return repository.findActiveByPersonaId(personaId);
    }

    @Override
    public List<PerfPersConjunto> listarActivasPorConjuntoIdYPersonaId(Integer conjuntoId, Integer personaId) {
        return repository.findActiveByConjIdAndPerId(conjuntoId, personaId);
    }

    @Override
    public Optional<PerfPersConjunto> buscarPorId(Integer conjuntoId, Integer personaId, Integer perfilId) {
        PerfPersConjuntoId id = new PerfPersConjuntoId();
        id.setPpcConjid(conjuntoId);
        id.setPpcPerid(personaId);
        id.setPpcPrfid(perfilId);
        return repository.findById(id);
    }

    @Override
    public PerfPersConjunto guardar(PerfPersConjunto relacion) {
        return repository.save(relacion);
    }

    @Override
    public void eliminar(Integer personaId, Integer conjuntoId, Integer perfilId) {
        buscarPorId(conjuntoId, personaId, perfilId)
                .ifPresent(ppc -> {
                    ppc.setPPCSts("I");
                    repository.save(ppc);
                });
    }

    @Override
    public Integer siguienteId() {
        Number maxId = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(ppcid), 0) + 1 FROM \"PerfPersConjunto\"")
                .getSingleResult();
        return maxId.intValue();
    }
}