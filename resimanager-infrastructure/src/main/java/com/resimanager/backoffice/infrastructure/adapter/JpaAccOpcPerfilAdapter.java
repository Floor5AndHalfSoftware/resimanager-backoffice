package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PermisoModulo;
import com.resimanager.backoffice.domain.port.out.AccOpcPerfilRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.AccOpcPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaAccOpcPerfilAdapter implements AccOpcPerfilRepositoryPort {

    private final AccOpcPerfilRepository repository;

    @Override
    public List<PermisoModulo> listarPermisosPorPerfilId(Integer perfilId) {
        return repository.findPermissionsByPerfilId(perfilId).stream()
                .map(row -> new PermisoModulo(
                        (Integer) row[0],
                        (String) row[1],
                        (String) row[2]))
                .toList();
    }

    @Override
    public boolean tienePermiso(Integer perfilId, String modulo, String accion) {
        return repository.hasPermission(perfilId, modulo, accion);
    }
}