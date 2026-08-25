package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.persistance.repository.ConjuntoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaConjuntoAdapter implements ConjuntoRepositoryPort {

    private final ConjuntoRepository repository;

    @Override
    public Optional<Conjunto> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public ResultadoPaginado<Conjunto> buscarConFiltros(Estatus estatus, String busqueda, int pagina, int limite) {
        PageRequest pageable = PageRequest.of(pagina - 1, limite);
        Page<Conjunto> result = repository.findAllWithFilters(
                estatus != null ? estatus.codigo() : null, busqueda, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pagina, limite);
    }

    @Override
    public List<Conjunto> buscarActivosPorIds(List<Integer> ids) {
        return repository.findActiveByIds(ids);
    }

    @Override
    public Conjunto guardar(Conjunto conjunto) {
        return repository.save(conjunto);
    }

    @Override
    public long contarActivas() {
        return repository.countByConjSts("A");
    }
}