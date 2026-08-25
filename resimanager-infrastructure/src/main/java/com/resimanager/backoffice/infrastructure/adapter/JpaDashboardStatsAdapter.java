package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.DashboardStats;
import com.resimanager.backoffice.domain.port.out.DashboardStatsRepositoryPort;
import com.resimanager.backoffice.infrastructure.persistence.repository.AdministradoraRepository;
import com.resimanager.backoffice.infrastructure.persistence.repository.ConjuntoRepository;
import com.resimanager.backoffice.infrastructure.persistence.repository.PersonaRepository;
import com.resimanager.backoffice.infrastructure.persistence.repository.PropiedadRepository;
import com.resimanager.backoffice.infrastructure.persistence.repository.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaDashboardStatsAdapter implements DashboardStatsRepositoryPort {

    private final AdministradoraRepository administradoraRepository;
    private final ConjuntoRepository conjuntoRepository;
    private final PersonaRepository personaRepository;
    private final PropiedadRepository propiedadRepository;
    private final PropietarioRepository propietarioRepository;

    @Override
    public DashboardStats obtenerEstadisticas() {
        return new DashboardStats(
                administradoraRepository.countByAdmSts("A"),
                conjuntoRepository.countByConjSts("A"),
                personaRepository.countByPerSts("A"),
                propiedadRepository.countByPpSts("A"),
                propietarioRepository.countByPptSts("A"),
                156, 98, 58,
                23, 8, 15
        );
    }
}