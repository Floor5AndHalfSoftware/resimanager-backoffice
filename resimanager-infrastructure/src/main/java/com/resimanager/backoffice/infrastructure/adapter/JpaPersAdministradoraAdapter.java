package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PersAdministradora;
import com.resimanager.backoffice.domain.port.out.PersAdministradoraRepositoryPort;
import com.resimanager.backoffice.persistance.repository.PersAdministradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaPersAdministradoraAdapter implements PersAdministradoraRepositoryPort {

    private final PersAdministradoraRepository repository;

    @Override
    public List<PersAdministradora> listarActivasPorPersonaId(Integer personaId) {
        return repository.findActiveByPersonaId(personaId);
    }

    @Override
    public List<PersAdministradora> listarActivasPorAdministradoraId(Integer administradoraId) {
        return repository.findActiveByAdmId(administradoraId);
    }

    @Override
    public PersAdministradora guardar(PersAdministradora relacion) {
        return repository.save(relacion);
    }
}