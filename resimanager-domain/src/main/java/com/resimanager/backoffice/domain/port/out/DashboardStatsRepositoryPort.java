package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.DashboardStats;

/**
 * Puerto de salida de obtención de estadísticas del dashboard.
 */
public interface DashboardStatsRepositoryPort {

    DashboardStats obtenerEstadisticas();
}