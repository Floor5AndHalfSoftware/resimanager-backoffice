package com.resimanager.backoffice.domain.port.in;

/**
 * Casos de uso de consulta de estadísticas del dashboard.
 */
public interface DashboardUseCase {

    com.resimanager.backoffice.domain.model.DashboardStats obtenerEstadisticas();
}