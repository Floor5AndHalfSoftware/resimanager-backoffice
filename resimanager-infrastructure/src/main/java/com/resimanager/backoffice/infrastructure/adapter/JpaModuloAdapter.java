package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.domain.port.out.ModuloRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaModuloAdapter implements ModuloRepositoryPort {

    private final ModuloRepository repository;

    @Override
    public List<Modulo> listarPorNivel(Integer nivel) {
        return nivel != null ? repository.findByModNivel(nivel) : repository.findByModSts("A");
    }

    @Override
    public List<Modulo> buscarActivosPorIds(List<Integer> ids) {
        return repository.findActiveByIds(ids);
    }

    @Override
    public List<Modulo> listarPorPerfilId(Integer perfilId) {
        return repository.findByPerfilId(perfilId);
    }

    @Override
    public Optional<Modulo> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return repository.existsById(id);
    }
}