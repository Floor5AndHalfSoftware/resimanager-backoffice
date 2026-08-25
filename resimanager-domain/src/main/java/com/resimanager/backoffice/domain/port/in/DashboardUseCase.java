package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.DashboardStats;

/**
 * Casos de uso de consulta de estadísticas del dashboard.
 */
public interface DashboardUseCase {

    DashboardStats obtenerEstadisticas();
}