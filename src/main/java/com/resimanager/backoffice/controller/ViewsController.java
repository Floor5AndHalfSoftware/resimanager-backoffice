package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@Validated
@Slf4j
public class ViewsController {

    private final MenuService menuService;

    public ViewsController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Get menus filtered by user's active profile
     * Requires the profile ID to be passed in the header
     * @param perfilId Active profile ID from JWT context
     * @return Filtered menu based on profile permissions
     */
    @GetMapping("/menu/perfil")
    public ResponseEntity<?> menuByPerfil(@RequestHeader(value = "X-Perfil-Id", required = false) Integer perfilId) {
        log.info("============ MENU ENDPOINT CALLED ============");
        log.info("Perfil ID received: {}", perfilId);
        
        if (perfilId == null) {
            log.warn("Menu request without profile ID");
            return ResponseEntity.badRequest().body("Profile ID is required. Please select a context first.");
        }
        
        log.info("Fetching menu for profile ID: {}", perfilId);
        List<MenuDto> menus = menuService.menusByPerfil(perfilId);
        log.info("Menu items found: {}", menus.size());
        return ResponseEntity.ok(menus);
    }
}
