package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPersonaAdapter implements PersonaRepositoryPort {

    private final PersonaRepository repository;

    @Override
    public Optional<Persona> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Persona> buscarPorUsuario(String usuario) {
        return repository.findByPerUsuario(usuario);
    }

    @Override
    public Optional<Persona> buscarPorUsuarioOEmail(String usuario, String email) {
        return repository.findByPerUsuarioOrPerEMail(usuario, email);
    }

    @Override
    public ResultadoPaginado<Persona> buscarConFiltros(Estatus estatus, String busqueda, int pagina, int limite) {
        PageRequest pageable = PageRequest.of(pagina - 1, limite);
        Page<Persona> result = repository.findAllWithFilters(
                estatus != null ? estatus.codigo() : null, busqueda, pageable);
        return new ResultadoPaginado<>(result.getContent(), result.getTotalElements(), pagina, limite);
    }

    @Override
    public List<Persona> buscarActivasPorIds(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Persona guardar(Persona persona) {
        return repository.save(persona);
    }
}