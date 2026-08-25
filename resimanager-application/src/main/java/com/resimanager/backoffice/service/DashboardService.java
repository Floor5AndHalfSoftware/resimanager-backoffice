package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.DashboardStats;
import com.resimanager.backoffice.domain.port.in.DashboardUseCase;
import com.resimanager.backoffice.domain.port.out.DashboardStatsRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements DashboardUseCase {

    private final DashboardStatsRepositoryPort dashboardStatsRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public DashboardStats obtenerEstadisticas() {
        return dashboardStatsRepositoryPort.obtenerEstadisticas();
    }
}