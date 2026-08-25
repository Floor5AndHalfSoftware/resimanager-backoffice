package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PerfPersAdministradora;
import com.resimanager.backoffice.domain.model.PerfPersAdministradoraId;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.PerfPersAdministradoraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPerfPersAdministradoraAdapter implements PerfPersAdministradoraRepositoryPort {

    private final PerfPersAdministradoraRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PerfPersAdministradora> listarActivasPorPersonaId(Integer personaId) {
        return repository.findActiveByPersonaId(personaId);
    }

    @Override
    public List<PerfPersAdministradora> listarActivasPorAdministradoraIdYPersonaId(Integer administradoraId, Integer personaId) {
        return repository.findActiveByAdmIdAndPerId(administradoraId, personaId);
    }

    @Override
    public Optional<PerfPersAdministradora> buscarPorId(Integer administradoraId, Integer personaId, Integer perfilId) {
        PerfPersAdministradoraId id = new PerfPersAdministradoraId();
        id.setPpaAdmid(administradoraId);
        id.setPpaPerid(personaId);
        id.setPpaPrfid(perfilId);
        return repository.findById(id);
    }

    @Override
    public PerfPersAdministradora guardar(PerfPersAdministradora relacion) {
        return repository.save(relacion);
    }

    @Override
    public void eliminar(Integer personaId, Integer administradoraId, Integer perfilId) {
        buscarPorId(administradoraId, personaId, perfilId)
                .ifPresent(ppa -> {
                    ppa.setPPASts("I");
                    repository.save(ppa);
                });
    }

    @Override
    public Integer siguienteId() {
        Number maxId = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(ppaid), 0) + 1 FROM \"PerfPersAdministradora\"")
                .getSingleResult();
        return maxId.intValue();
    }
}