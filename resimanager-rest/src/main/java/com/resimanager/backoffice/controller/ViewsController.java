package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Menú", description = "Obtención del menú lateral filtrado por perfil y permisos")
public class ViewsController {

    private final MenuService menuService;

    public ViewsController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(
            summary = "Obtener menú por perfil",
            description = """
                    Devuelve el árbol de items del menú lateral **filtrados según los permisos del perfil activo**.

                    Los items se filtran en base a los módulos asignados al perfil (`ModPerfil`).
                    Los items sin módulo asignado (Dashboard, agrupadores) siempre se incluyen.

                    La respuesta es una lista plana de `MenuDto`. Cada item incluye su lista de `submenus`
                    construida jerárquicamente por el servicio.

                    **Requiere** el header `X-Perfil-Id` con el ID del perfil activo (obtenido al hacer cambio de contexto).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de items de menú autorizados para el perfil",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuDto.class)))),
            @ApiResponse(responseCode = "400", description = "Header X-Perfil-Id ausente"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o expirado")
    })
    @GetMapping("/menu/perfil")
    public ResponseEntity<?> menuByPerfil(
            @Parameter(description = "ID del perfil activo del usuario. Se obtiene tras el cambio de contexto.", required = true, example = "2")
            @RequestHeader(value = "X-Perfil-Id", required = false) Integer perfilId) {
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
