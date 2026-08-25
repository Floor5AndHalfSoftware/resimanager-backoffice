package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.PropiedadRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPropiedadAdapter implements PropiedadRepositoryPort {

    private final PropiedadRepository repository;

    @Override
    public Optional<Propiedad> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Propiedad> buscarPorPpidYConjuntoId(Integer ppid, Integer conjuntoId) {
        return repository.findByPpidAndPpConjId(ppid, conjuntoId);
    }

    @Override
    public ResultadoPaginado<Propiedad> buscarConFiltros(Estatus estatus, Integer conjuntoId,
                                                         String busqueda, int pagina, int limite) {
        PageRequest pageable = PageRequest.of(pagina - 1, limite);
        Page<Propiedad> result = repository.findAllWithFilters(
                estatus != null ? estatus.codigo() : null, conjuntoId, busqueda, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pagina, limite);
    }

    @Override
    public Propiedad guardar(Propiedad propiedad) {
        return repository.save(propiedad);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public long contarActivas() {
        return repository.countByPpSts("A");
    }
}