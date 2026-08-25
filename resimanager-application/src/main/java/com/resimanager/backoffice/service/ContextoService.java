package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.PerfPersAdministradora;
import com.resimanager.backoffice.domain.model.PerfPersConjunto;
import com.resimanager.backoffice.domain.model.PersAdministradora;
import com.resimanager.backoffice.domain.model.PersConjunto;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersConjuntoRepositoryPort;
import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.ContextoDTO;
import com.resimanager.backoffice.dto.PerfilDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextoService {

    private final PersAdministradoraRepositoryPort persAdministradoraRepositoryPort;
    private final PersConjuntoRepositoryPort persConjuntoRepositoryPort;
    private final PerfPersAdministradoraRepositoryPort perfPersAdministradoraRepositoryPort;
    private final PerfPersConjuntoRepositoryPort perfPersConjuntoRepositoryPort;
    private final AdministradoraRepositoryPort administradoraRepositoryPort;
    private final ConjuntoRepositoryPort conjuntoRepositoryPort;

    @Transactional(readOnly = true)
    public List<ContextoDTO> getContextosDisponibles(Integer personaId) {
        List<ContextoDTO> contextos = new ArrayList<>();
        contextos.addAll(getContextosAdministradora(personaId));
        contextos.addAll(getContextosConjunto(personaId));
        return contextos;
    }

    private List<ContextoDTO> getContextosAdministradora(Integer personaId) {
        List<PersAdministradora> relaciones = persAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);
        if (relaciones.isEmpty()) {
            return List.of();
        }

        List<Integer> adminIds = relaciones.stream()
                .map(pa -> pa.getId().getPaAdmid())
                .distinct()
                .collect(Collectors.toList());

        List<Administradora> administradoras = administradoraRepositoryPort.buscarActivasPorIds(adminIds);
        Map<Integer, Administradora> adminMap = administradoras.stream()
                .collect(Collectors.toMap(Administradora::getId, a -> a));

        List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);
        Map<Integer, List<PerfPersAdministradora>> perfilesPorAdmin = perfiles.stream()
                .collect(Collectors.groupingBy(p -> p.getId().getPpaAdmid()));

        List<ContextoDTO> contextos = new ArrayList<>();
        for (Integer adminId : adminIds) {
            Administradora admin = adminMap.get(adminId);
            if (admin == null) continue;

            List<PerfilDTO> perfilesDTO = perfilesPorAdmin.getOrDefault(adminId, List.of()).stream()
                    .map(p -> PerfilDTO.builder()
                            .id(p.getPpaPrfid().getId())
                            .nombre(p.getPpaPrfid().getPrfNombre())
                            .descripcion(p.getPpaPrfid().getPrfDescrip())
                            .build())
                    .collect(Collectors.toList());

            contextos.add(ContextoDTO.builder()
                    .tipo("ADMINISTRADORA")
                    .administradora(AdministradoraDTO.builder()
                            .id(admin.getId())
                            .nombre(admin.getAdmNombre())
                            .documento(admin.getAdmDocIdent())
                            .email(admin.getAdmEMail())
                            .build())
                    .perfilesDisponibles(perfilesDTO)
                    .build());
        }

        return contextos;
    }

    private List<ContextoDTO> getContextosConjunto(Integer personaId) {
        List<PersConjunto> relaciones = persConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        if (relaciones.isEmpty()) {
            return List.of();
        }

        List<Integer> conjIds = relaciones.stream()
                .map(pc -> pc.getId().getPcConjid())
                .distinct()
                .collect(Collectors.toList());

        List<Conjunto> conjuntos = conjuntoRepositoryPort.buscarActivosPorIds(conjIds);
        Map<Integer, Conjunto> conjMap = conjuntos.stream()
                .collect(Collectors.toMap(Conjunto::getId, c -> c));

        List<PerfPersConjunto> perfiles = perfPersConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        Map<Integer, List<PerfPersConjunto>> perfilesPorConj = perfiles.stream()
                .collect(Collectors.groupingBy(p -> p.getId().getPpcConjid()));

        List<ContextoDTO> contextos = new ArrayList<>();
        for (Integer conjId : conjIds) {
            Conjunto conj = conjMap.get(conjId);
            if (conj == null) continue;

            List<PerfilDTO> perfilesDTO = perfilesPorConj.getOrDefault(conjId, List.of()).stream()
                    .map(p -> PerfilDTO.builder()
                            .id(p.getPpcPrfid().getId())
                            .nombre(p.getPpcPrfid().getPrfNombre())
                            .descripcion(p.getPpcPrfid().getPrfDescrip())
                            .build())
                    .collect(Collectors.toList());

            contextos.add(ContextoDTO.builder()
                    .tipo("CONJUNTO")
                    .conjunto(ConjuntoDTO.builder()
                            .id(conj.getId())
                            .nombre(conj.getConjNombre())
                            .documento(conj.getConjDocIdent())
                            .build())
                    .perfilesDisponibles(perfilesDTO)
                    .build());
        }

        return contextos;
    }

    public List<String> getRolesFromProfiles(Integer personaId) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        List<PerfPersAdministradora> perfilesAdmin = perfPersAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);
        for (PerfPersAdministradora ppa : perfilesAdmin) {
            String roleName = "ROLE_" + ppa.getPpaPrfid().getPrfNombre().toUpperCase().replace(" ", "_");
            if (!roles.contains(roleName)) {
                roles.add(roleName);
            }
        }

        List<PerfPersConjunto> perfilesConj = perfPersConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        for (PerfPersConjunto ppc : perfilesConj) {
            String roleName = "ROLE_" + ppc.getPpcPrfid().getPrfNombre().toUpperCase().replace(" ", "_");
            if (!roles.contains(roleName)) {
                roles.add(roleName);
            }
        }

        return roles;
    }

    @Transactional(readOnly = true)
    public ContextoActualDTO validarYConstruirContexto(Integer personaId, String tipo, Integer entidadId, Integer perfilId) {
        if ("ADMINISTRADORA".equals(tipo)) {
            return validarContextoAdministradora(personaId, entidadId, perfilId);
        } else if ("CONJUNTO".equals(tipo)) {
            return validarContextoConjunto(personaId, entidadId, perfilId);
        } else {
            throw new IllegalArgumentException("Tipo de contexto inválido: " + tipo);
        }
    }

    private ContextoActualDTO validarContextoAdministradora(Integer personaId, Integer adminId, Integer perfilId) {
        List<PersAdministradora> relaciones = persAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);

        boolean tieneAcceso = relaciones.stream()
                .anyMatch(pa -> pa.getId().getPaAdmid().equals(adminId));

        if (!tieneAcceso) {
            throw new IllegalArgumentException("El usuario no tiene acceso a la administradora especificada");
        }

        List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);
        PerfPersAdministradora perfilActivo = perfiles.stream()
                .filter(p -> p.getId().getPpaAdmid().equals(adminId) && p.getPpaPrfid().getId().equals(perfilId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El perfil especificado no está asignado al usuario en esta administradora"));

        Administradora admin = administradoraRepositoryPort.buscarPorId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Administradora no encontrada"));

        return ContextoActualDTO.builder()
                .tipo("ADMINISTRADORA")
                .entidadId(adminId)
                .entidadNombre(admin.getAdmNombre())
                .perfilId(perfilId)
                .perfilNombre(perfilActivo.getPpaPrfid().getPrfNombre())
                .perfilDescripcion(perfilActivo.getPpaPrfid().getPrfDescrip())
                .build();
    }

    private ContextoActualDTO validarContextoConjunto(Integer personaId, Integer conjId, Integer perfilId) {
        List<PersConjunto> relaciones = persConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        boolean tieneAcceso = relaciones.stream()
                .anyMatch(pc -> pc.getId().getPcConjid().equals(conjId));

        if (!tieneAcceso) {
            throw new IllegalArgumentException("El usuario no tiene acceso al conjunto especificado");
        }

        List<PerfPersConjunto> perfiles = perfPersConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        PerfPersConjunto perfilActivo = perfiles.stream()
                .filter(p -> p.getId().getPpcConjid().equals(conjId) && p.getPpcPrfid().getId().equals(perfilId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El perfil especificado no está asignado al usuario en este conjunto"));

        Conjunto conj = conjuntoRepositoryPort.buscarPorId(conjId)
                .orElseThrow(() -> new IllegalArgumentException("Conjunto no encontrado"));

        return ContextoActualDTO.builder()
                .tipo("CONJUNTO")
                .entidadId(conjId)
                .entidadNombre(conj.getConjNombre())
                .perfilId(perfilId)
                .perfilNombre(perfilActivo.getPpcPrfid().getPrfNombre())
                .perfilDescripcion(perfilActivo.getPpcPrfid().getPrfDescrip())
                .build();
    }
}