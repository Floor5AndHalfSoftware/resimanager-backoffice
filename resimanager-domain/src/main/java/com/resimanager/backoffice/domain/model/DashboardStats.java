package com.resimanager.backoffice.domain.model;

/**
 * Estadísticas agregadas para el dashboard.
 */
public record DashboardStats(
        long totalAdministradoras,
        long totalConjuntos,
        long totalUsuarios,
        long totalPropiedades,
        long totalPropietarios,
        long totalFacturas,
        long totalFacturasPagadas,
        long totalFacturasPendientes,
        long totalIncidencias,
        long totalIncidenciasAbiertas,
        long totalIncidenciasCerradas
) {

    public static DashboardStats vacio() {
        return new DashboardStats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}