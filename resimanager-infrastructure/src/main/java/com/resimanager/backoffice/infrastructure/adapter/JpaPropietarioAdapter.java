package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.PropietarioId;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.PropietarioRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPropietarioAdapter implements PropietarioRepositoryPort {

    private final PropietarioRepository repository;

    private PropietarioId id(Integer conjuntoId, Integer personaId) {
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjuntoId);
        id.setPptPerid(personaId);
        return id;
    }

    @Override
    public Optional<Propietario> buscarPorId(Integer conjuntoId, Integer personaId) {
        return repository.findById(id(conjuntoId, personaId));
    }

    @Override
    public boolean existePorId(Integer conjuntoId, Integer personaId) {
        return repository.existsById(id(conjuntoId, personaId));
    }

    @Override
    public ResultadoPaginado<Propietario> buscarConFiltros(Estatus estatus, Integer conjuntoId,
                                                           String busqueda, int pagina, int limite) {
        PageRequest pageable = PageRequest.of(pagina - 1, limite);
        Page<Propietario> result = repository.findAllWithFilters(
                estatus != null ? estatus.codigo() : null, conjuntoId, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pagina, limite);
    }

    @Override
    public Propietario guardar(Propietario propietario) {
        return repository.save(propietario);
    }

    @Override
    public void eliminar(Integer conjuntoId, Integer personaId) {
        repository.deleteById(id(conjuntoId, personaId));
    }

    @Override
    public long contarActivas() {
        return repository.countByPptSts("A");
    }
}