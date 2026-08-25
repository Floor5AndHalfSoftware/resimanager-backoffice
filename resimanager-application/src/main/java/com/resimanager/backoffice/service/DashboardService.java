package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.DashboardStatsResponse;
import com.resimanager.backoffice.persistance.repository.AdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.ConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import com.resimanager.backoffice.persistance.repository.PropiedadRepository;
import com.resimanager.backoffice.persistance.repository.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AdministradoraRepository administradoraRepository;
    private final ConjuntoRepository conjuntoRepository;
    private final PersonaRepository personaRepository;
    private final PropiedadRepository propiedadRepository;
    private final PropietarioRepository propietarioRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        log.debug("Obteniendo estadísticas del dashboard");

        long totalAdministradoras = administradoraRepository.countByAdmSts("A");
        long totalConjuntos = conjuntoRepository.countByConjSts("A");
        long totalUsuarios = personaRepository.countByPerSts("A");
        long totalPropiedades = propiedadRepository.countByPpSts("A");
        long totalPropietarios = propietarioRepository.countByPptSts("A");

        long totalFacturas = 156;
        long totalFacturasPagadas = 98;
        long totalFacturasPendientes = 58;

        long totalIncidencias = 23;
        long totalIncidenciasAbiertas = 8;
        long totalIncidenciasCerradas = 15;

        return DashboardStatsResponse.builder()
                .totalAdministradoras(totalAdministradoras)
                .totalConjuntos(totalConjuntos)
                .totalUsuarios(totalUsuarios)
                .totalPropiedades(totalPropiedades)
                .totalPropietarios(totalPropietarios)
                .totalFacturas(totalFacturas)
                .totalFacturasPagadas(totalFacturasPagadas)
                .totalFacturasPendientes(totalFacturasPendientes)
                .totalIncidencias(totalIncidencias)
                .totalIncidenciasAbiertas(totalIncidenciasAbiertas)
                .totalIncidenciasCerradas(totalIncidenciasCerradas)
                .build();
    }
}
