package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.domain.model.MenuItem;
import com.resimanager.backoffice.domain.model.MenuItemId;
import com.resimanager.backoffice.domain.port.out.MenuRepositoryPort;
import com.resimanager.backoffice.service.mapper.MnuToMnuDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MenuService {

    private final MenuRepositoryPort menuRepositoryPort;

    public MenuService(MenuRepositoryPort menuRepositoryPort) {
        this.menuRepositoryPort = menuRepositoryPort;
    }

    public List<MenuDto> menus(){
        List<MenuItem> menuItems = menuRepositoryPort.listarMenu();
        List<MenuDto> menusDto = new ArrayList<>();

        for (MenuItem menuItem : menuItems) {
            List<MenuDto> menusHijosDto = new ArrayList<>();
            MenuDto menuDto = new MnuToMnuDtoMapper().apply(menuItem);
            List<MenuItem> menusHijos = subMenus(menuItem.getId());
            for (MenuItem menuHijo : menusHijos) {
                MenuDto menuHijoDto = new MnuToMnuDtoMapper().apply(menuHijo);
                menuHijoDto = menuHijoDto.toBuilder().submenus(new ArrayList<>()).build();
                menusHijosDto.add(menuHijoDto);
            }
            menuDto = menuDto.toBuilder().submenus(menusHijosDto).build();
            menusDto.add(menuDto);
        }
        return menusDto;
    }

    public List<MenuDto> menusByPerfil(Integer perfilId) {
        if (perfilId == 1) {
            return menus();
        }

        List<MenuItem> menuItems = menuRepositoryPort.listarMenuPorPerfil(perfilId);
        return buildMenuHierarchy(menuItems);
    }

    private List<MenuDto> buildMenuHierarchy(List<MenuItem> menuItems) {
        Map<Integer, MenuDto> menuMap = new HashMap<>();
        List<MenuDto> rootMenus = new ArrayList<>();

        MnuToMnuDtoMapper mapper = new MnuToMnuDtoMapper();
        for (MenuItem item : menuItems) {
            MenuDto dto = mapper.apply(item);
            dto = dto.toBuilder().submenus(new ArrayList<>()).build();
            menuMap.put(dto.itemId(), dto);
        }

        for (MenuDto dto : menuMap.values()) {
            if (dto.idPadre() == 0) {
                rootMenus.add(dto);
            } else {
                MenuDto parent = menuMap.get(dto.idPadre());
                if (parent != null) {
                    List<MenuDto> parentSubmenus = new ArrayList<>(parent.submenus());
                    parentSubmenus.add(dto);
                    MenuDto updatedParent = parent.toBuilder().submenus(parentSubmenus).build();
                    menuMap.put(parent.itemId(), updatedParent);
                } else {
                    rootMenus.add(dto);
                }
            }
        }

        List<MenuDto> sortedRoots = new ArrayList<>(menuMap.values()).stream()
            .filter(dto -> dto.idPadre() == 0)
            .sorted((a, b) -> Integer.compare(a.orden(), b.orden()))
            .toList();

        for (MenuDto menu : sortedRoots) {
            sortSubmenus(menu);
        }

        rootMenus.clear();
        rootMenus.addAll(sortedRoots);

        return rootMenus;
    }

    private MenuDto sortSubmenus(MenuDto menu) {
        if (menu.submenus() != null && !menu.submenus().isEmpty()) {
            List<MenuDto> sorted = menu.submenus().stream()
                .map(this::sortSubmenus)
                .sorted((a, b) -> Integer.compare(a.orden(), b.orden()))
                .toList();
            return menu.toBuilder().submenus(sorted).build();
        }
        return menu;
    }

    public List<MenuItem> subMenus(MenuItemId menuid){
        return menuRepositoryPort.listarSubMenus(menuid.getMItID());
    }
}