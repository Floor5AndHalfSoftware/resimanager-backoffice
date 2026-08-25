package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.DashboardStats;
import com.resimanager.backoffice.domain.port.out.DashboardStatsRepositoryPort;
import com.resimanager.backoffice.dto.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final DashboardStatsRepositoryPort dashboardStatsRepositoryPort;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        DashboardStats s = dashboardStatsRepositoryPort.obtenerEstadisticas();

        return DashboardStatsResponse.builder()
                .totalAdministradoras(s.totalAdministradoras())
                .totalConjuntos(s.totalConjuntos())
                .totalUsuarios(s.totalUsuarios())
                .totalPropiedades(s.totalPropiedades())
                .totalPropietarios(s.totalPropietarios())
                .totalFacturas(s.totalFacturas())
                .totalFacturasPagadas(s.totalFacturasPagadas())
                .totalFacturasPendientes(s.totalFacturasPendientes())
                .totalIncidencias(s.totalIncidencias())
                .totalIncidenciasAbiertas(s.totalIncidenciasAbiertas())
                .totalIncidenciasCerradas(s.totalIncidenciasCerradas())
                .build();
    }
}