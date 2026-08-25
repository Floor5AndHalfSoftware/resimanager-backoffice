package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.persistance.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPerfilAdapter implements PerfilRepositoryPort {

    private final PerfilRepository repository;

    @Override
    public Optional<Perfil> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public ResultadoPaginado<Perfil> buscarConFiltros(Estatus estatus, Integer nivel, String busqueda,
                                                      int pagina, int limite) {
        int pageNumber = (pagina > 0) ? pagina - 1 : 0;
        int pageSize = (limite > 0) ? limite : 25;
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Perfil> result = repository.findWithFilters(
                estatus != null ? estatus.codigo() : null, nivel, busqueda, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pageNumber + 1, pageSize);
    }

    @Override
    public List<Perfil> buscarActivosPorIds(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Perfil guardar(Perfil perfil) {
        return repository.save(perfil);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return repository.existsByPrfNombre(nombre);
    }

    @Override
    public boolean existePorNombreYDistintoId(String nombre, Integer id) {
        return repository.existsByPrfNombreAndIdNot(nombre, id);
    }

    @Override
    public long contarUsuariosAsignados(Integer perfilId) {
        return repository.countUsuariosAsignados(perfilId);
    }
}