package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.AdministradoraRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaAdministradoraAdapter implements AdministradoraRepositoryPort {

    private final AdministradoraRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Administradora> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public ResultadoPaginado<Administradora> buscarConFiltros(Estatus estatus, String busqueda, int pagina, int limite) {
        PageRequest pageable = PageRequest.of(pagina - 1, limite);
        Page<Administradora> result = repository.findAllWithFilters(
                estatus != null ? estatus.codigo() : null, busqueda, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pagina, limite);
    }

    @Override
    public List<Administradora> buscarActivasPorIds(List<Integer> ids) {
        return repository.findActiveByIds(ids);
    }

    @Override
    public Administradora guardar(Administradora administradora) {
        return repository.save(administradora);
    }

    @Override
    public Integer siguienteId() {
        Number maxId = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(admid), 0) + 1 FROM \"Administradora\"")
                .getSingleResult();
        return maxId.intValue();
    }

    @Override
    public long contarActivas() {
        return repository.countByAdmSts("A");
    }
}