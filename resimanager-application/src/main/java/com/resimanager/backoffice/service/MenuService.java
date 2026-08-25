package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.persistance.entity.MenuItem;
import com.resimanager.backoffice.persistance.entity.MenuItemId;
import com.resimanager.backoffice.persistance.repository.MenuItemRepository;
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

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Get all menus (unfiltered - for backward compatibility)
     */
    public List<MenuDto> menus(){
        List<MenuItem> menuItems = menuItemRepository.findMenus();
        List<MenuDto> menusDto = new ArrayList<>();

        for (MenuItem menuItem : menuItems) {
            List<MenuDto> menusHijosDto = new ArrayList<>();
            MenuDto menuDto = new MnuToMnuDtoMapper().apply(menuItem);
            List<MenuItem> menusHijos= subMenus(menuItem.getId());
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
    
    /**
     * Get menus filtered by user's active profile permissions
     * @param perfilId Active profile ID from JWT context
     * @return Hierarchical menu structure with only accessible items
     */
    public List<MenuDto> menusByPerfil(Integer perfilId) {
        log.debug("Fetching menus for profile ID: {}", perfilId);
        
        // Profile 1 (Super Admin) gets all menus
        if (perfilId == 1) {
            log.debug("Super admin detected, returning all menus");
            return menus();
        }
        
        // Get menu items accessible by this profile
        List<MenuItem> menuItems = menuItemRepository.findMenusByPerfil(perfilId);
        
        log.debug("Found {} menu items for profile {}", menuItems.size(), perfilId);
        
        // Build hierarchical structure
        return buildMenuHierarchy(menuItems);
    }
    
    /**
     * Build hierarchical menu structure from flat list
     */
    private List<MenuDto> buildMenuHierarchy(List<MenuItem> menuItems) {
        Map<Integer, MenuDto> menuMap = new HashMap<>();
        List<MenuDto> rootMenus = new ArrayList<>();
        
        // Convert all items to DTOs
        MnuToMnuDtoMapper mapper = new MnuToMnuDtoMapper();
        for (MenuItem item : menuItems) {
            MenuDto dto = mapper.apply(item);
            dto = dto.toBuilder().submenus(new ArrayList<>()).build();
            menuMap.put(dto.itemId(), dto);
        }
        
        // Build hierarchy
        for (MenuDto dto : menuMap.values()) {
            if (dto.idPadre() == 0) {
                // Root level item
                rootMenus.add(dto);
            } else {
                // Child item - add to parent's submenu
                MenuDto parent = menuMap.get(dto.idPadre());
                if (parent != null) {
                    List<MenuDto> parentSubmenus = new ArrayList<>(parent.submenus());
                    parentSubmenus.add(dto);
                    MenuDto updatedParent = parent.toBuilder().submenus(parentSubmenus).build();
                    menuMap.put(parent.itemId(), updatedParent);
                } else {
                    // Parent not accessible by this profile, add as root
                    rootMenus.add(dto);
                }
            }
        }
        
        // Sort by order
        List<MenuDto> sortedRoots = new ArrayList<>(menuMap.values()).stream()
            .filter(dto -> dto.idPadre() == 0)
            .sorted((a, b) -> Integer.compare(a.orden(), b.orden()))
            .toList();
        
        for (MenuDto menu : sortedRoots) {
            sortSubmenus(menu);
        }
        
        // Rebuild rootMenus with sorted versions
        rootMenus.clear();
        rootMenus.addAll(sortedRoots);
        
        return rootMenus;
    }
    
    /**
     * Recursively sort submenus (returns new sorted version)
     */
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
        return menuItemRepository.findSubMenus(menuid.getMItID());
    }
}
