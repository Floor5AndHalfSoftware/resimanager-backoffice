package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalAdministradoras;
    private Long totalConjuntos;
    private Long totalUsuarios;
    private Long totalPropiedades;
    private Long totalPropietarios;
    private Long totalFacturas;
    private Long totalFacturasPagadas;
    private Long totalFacturasPendientes;
    private Long totalIncidencias;
    private Long totalIncidenciasAbiertas;
    private Long totalIncidenciasCerradas;
}
