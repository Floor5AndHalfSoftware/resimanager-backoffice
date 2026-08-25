package com.resimanager.backoffice.dto;

import lombok.Builder;

@Builder
public record DashboardStatsResponse(
    Long totalAdministradoras,
    Long totalConjuntos,
    Long totalUsuarios,
    Long totalPropiedades,
    Long totalPropietarios,
    Long totalFacturas,
    Long totalFacturasPagadas,
    Long totalFacturasPendientes,
    Long totalIncidencias,
    Long totalIncidenciasAbiertas,
    Long totalIncidenciasCerradas
) {}
