package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.MenuItem;
import com.resimanager.backoffice.domain.port.out.MenuRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaMenuAdapter implements MenuRepositoryPort {

    private final MenuItemRepository repository;

    @Override
    public List<MenuItem> listarMenu() {
        return repository.findMenus();
    }

    @Override
    public List<MenuItem> listarMenuPorPerfil(Integer perfilId) {
        return repository.findMenusByPerfil(perfilId);
    }

    @Override
    public List<MenuItem> listarSubMenus(Integer menuId) {
        return repository.findSubMenus(menuId);
    }
}