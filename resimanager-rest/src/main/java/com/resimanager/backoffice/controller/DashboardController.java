package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.dto.DashboardStatsResponse;
import com.resimanager.backoffice.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH + "/dashboard")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Estadísticas y resumen del dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Obtener estadísticas del dashboard",
            description = "Devuelve conteos de entidades del sistema (administradoras, conjuntos, usuarios, propiedades, propietarios) y datos mock de facturas e incidencias."
    )
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        log.debug("GET /dashboard/stats");
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
