package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.ModPerfil;
import com.resimanager.backoffice.domain.model.ModPerfilId;
import com.resimanager.backoffice.domain.port.out.ModPerfilRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.ModPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaModPerfilAdapter implements ModPerfilRepositoryPort {

    private final ModPerfilRepository repository;

    @Override
    public List<ModPerfil> listarActivosPorPerfilId(Integer perfilId) {
        return repository.findActiveByPerfilId(perfilId);
    }

    @Override
    public Optional<ModPerfil> buscarPorId(Integer perfilId, Integer moduloId) {
        return Optional.ofNullable(repository.findByPerfilIdAndModuloId(perfilId, moduloId));
    }

    @Override
    public ModPerfil guardar(ModPerfil asignacion) {
        return repository.save(asignacion);
    }

    @Override
    public void eliminar(Integer perfilId, Integer moduloId) {
        repository.deleteByPerfilIdAndModuloId(perfilId, moduloId);
    }
}