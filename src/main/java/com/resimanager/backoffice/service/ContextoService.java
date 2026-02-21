package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.ContextoDTO;
import com.resimanager.backoffice.dto.PerfilDTO;
import com.resimanager.backoffice.persistance.entity.*;
import com.resimanager.backoffice.persistance.repository.*;
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

    private final PersAdministradoraRepository persAdministradoraRepository;
    private final PersConjuntoRepository persConjuntoRepository;
    private final PerfPersAdministradoraRepository perfPersAdministradoraRepository;
    private final PerfPersConjuntoRepository perfPersConjuntoRepository;
    private final AdministradoraRepository administradoraRepository;
    private final ConjuntoRepository conjuntoRepository;

    /**
     * Obtiene todos los contextos disponibles para un usuario
     * @param personaId ID de la persona
     * @return Lista de contextos (Administradoras y Conjuntos) con sus perfiles
     */
    @Transactional(readOnly = true)
    public List<ContextoDTO> getContextosDisponibles(Integer personaId) {
        log.debug("Obteniendo contextos para persona ID: {}", personaId);
        
        List<ContextoDTO> contextos = new ArrayList<>();

        // 1. Obtener contextos de Administradora
        contextos.addAll(getContextosAdministradora(personaId));

        // 2. Obtener contextos de Conjunto
        contextos.addAll(getContextosConjunto(personaId));

        log.info("Usuario {} tiene {} contextos disponibles", personaId, contextos.size());
        return contextos;
    }

    /**
     * Obtiene los contextos de Administradora para un usuario
     */
    private List<ContextoDTO> getContextosAdministradora(Integer personaId) {
        // Obtener las administradoras del usuario
        List<PersAdministradora> relaciones = persAdministradoraRepository.findActiveByPersonaId(personaId);
        
        if (relaciones.isEmpty()) {
            return List.of();
        }

        // Obtener IDs de administradoras
        List<Integer> adminIds = relaciones.stream()
                .map(pa -> pa.getId().getPaAdmid())
                .distinct()
                .collect(Collectors.toList());

        // Obtener datos de administradoras
        List<Administradora> administradoras = administradoraRepository.findActiveByIds(adminIds);
        Map<Integer, Administradora> adminMap = administradoras.stream()
                .collect(Collectors.toMap(Administradora::getId, a -> a));

        // Obtener perfiles del usuario en cada administradora
        List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepository.findActiveByPersonaId(personaId);
        
        // Agrupar perfiles por administradora
        Map<Integer, List<PerfPersAdministradora>> perfilesPorAdmin = perfiles.stream()
                .collect(Collectors.groupingBy(p -> p.getId().getPpaAdmid()));

        // Construir DTOs
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

    /**
     * Obtiene los contextos de Conjunto para un usuario
     */
    private List<ContextoDTO> getContextosConjunto(Integer personaId) {
        // Obtener los conjuntos del usuario
        List<PersConjunto> relaciones = persConjuntoRepository.findActiveByPersonaId(personaId);
        
        if (relaciones.isEmpty()) {
            return List.of();
        }

        // Obtener IDs de conjuntos
        List<Integer> conjIds = relaciones.stream()
                .map(pc -> pc.getId().getPcConjid())
                .distinct()
                .collect(Collectors.toList());

        // Obtener datos de conjuntos
        List<Conjunto> conjuntos = conjuntoRepository.findActiveByIds(conjIds);
        Map<Integer, Conjunto> conjMap = conjuntos.stream()
                .collect(Collectors.toMap(Conjunto::getId, c -> c));

        // Obtener perfiles del usuario en cada conjunto
        List<PerfPersConjunto> perfiles = perfPersConjuntoRepository.findActiveByPersonaId(personaId);
        
        // Agrupar perfiles por conjunto
        Map<Integer, List<PerfPersConjunto>> perfilesPorConj = perfiles.stream()
                .collect(Collectors.groupingBy(p -> p.getId().getPpcConjid()));

        // Construir DTOs
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

    /**
     * Obtiene los roles/authorities del usuario basado en sus perfiles
     * @param personaId ID de la persona
     * @return Set de authorities para Spring Security
     */
    public List<String> getRolesFromProfiles(Integer personaId) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER"); // Rol base para todos los usuarios

        // Obtener perfiles de administradora
        List<PerfPersAdministradora> perfilesAdmin = perfPersAdministradoraRepository.findActiveByPersonaId(personaId);
        for (PerfPersAdministradora ppa : perfilesAdmin) {
            String roleName = "ROLE_" + ppa.getPpaPrfid().getPrfNombre().toUpperCase().replace(" ", "_");
            if (!roles.contains(roleName)) {
                roles.add(roleName);
            }
        }

        // Obtener perfiles de conjunto
        List<PerfPersConjunto> perfilesConj = perfPersConjuntoRepository.findActiveByPersonaId(personaId);
        for (PerfPersConjunto ppc : perfilesConj) {
            String roleName = "ROLE_" + ppc.getPpcPrfid().getPrfNombre().toUpperCase().replace(" ", "_");
            if (!roles.contains(roleName)) {
                roles.add(roleName);
            }
        }

        log.debug("Usuario {} tiene roles: {}", personaId, roles);
        return roles;
    }
    
    /**
     * Valida y construye el contexto actual del usuario
     * @param personaId ID de la persona
     * @param tipo Tipo de contexto (ADMINISTRADORA o CONJUNTO)
     * @param entidadId ID de la entidad (administradora o conjunto)
     * @param perfilId ID del perfil a usar
     * @return ContextoActualDTO con la información del contexto activo
     * @throws IllegalArgumentException si el contexto no es válido
     */
    @Transactional(readOnly = true)
    public ContextoActualDTO validarYConstruirContexto(Integer personaId, String tipo, Integer entidadId, Integer perfilId) {
        log.debug("Validando contexto para persona {}: tipo={}, entidadId={}, perfilId={}", 
                personaId, tipo, entidadId, perfilId);
        
        if ("ADMINISTRADORA".equals(tipo)) {
            return validarContextoAdministradora(personaId, entidadId, perfilId);
        } else if ("CONJUNTO".equals(tipo)) {
            return validarContextoConjunto(personaId, entidadId, perfilId);
        } else {
            throw new IllegalArgumentException("Tipo de contexto inválido: " + tipo);
        }
    }
    
    private ContextoActualDTO validarContextoAdministradora(Integer personaId, Integer adminId, Integer perfilId) {
        // Verificar que la persona tiene acceso a esta administradora
        List<PersAdministradora> relaciones = persAdministradoraRepository.findActiveByPersonaId(personaId);
        log.debug("Relaciones de persona {} con administradoras: {}", personaId, 
            relaciones.stream().map(r -> r.getId().getPaAdmid()).collect(java.util.stream.Collectors.toList()));
        
        boolean tieneAcceso = relaciones.stream()
                .anyMatch(pa -> pa.getId().getPaAdmid().equals(adminId));
        
        if (!tieneAcceso) {
            log.error("Usuario {} NO tiene acceso a administradora {}. Administradoras disponibles: {}", 
                personaId, adminId, relaciones.stream().map(r -> r.getId().getPaAdmid()).collect(java.util.stream.Collectors.toList()));
            throw new IllegalArgumentException("El usuario no tiene acceso a la administradora especificada");
        }
        
        // Verificar que el perfil existe y está activo en esta administradora
        List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepository.findActiveByPersonaId(personaId);
        PerfPersAdministradora perfilActivo = perfiles.stream()
                .filter(p -> p.getId().getPpaAdmid().equals(adminId) && p.getPpaPrfid().getId().equals(perfilId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El perfil especificado no está asignado al usuario en esta administradora"));
        
        // Obtener información de la administradora
        Administradora admin = administradoraRepository.findById(adminId)
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
        // Verificar que la persona tiene acceso a este conjunto
        List<PersConjunto> relaciones = persConjuntoRepository.findActiveByPersonaId(personaId);
        boolean tieneAcceso = relaciones.stream()
                .anyMatch(pc -> pc.getId().getPcConjid().equals(conjId));
        
        if (!tieneAcceso) {
            throw new IllegalArgumentException("El usuario no tiene acceso al conjunto especificado");
        }
        
        // Verificar que el perfil existe y está activo en este conjunto
        List<PerfPersConjunto> perfiles = perfPersConjuntoRepository.findActiveByPersonaId(personaId);
        PerfPersConjunto perfilActivo = perfiles.stream()
                .filter(p -> p.getId().getPpcConjid().equals(conjId) && p.getPpcPrfid().getId().equals(perfilId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El perfil especificado no está asignado al usuario en este conjunto"));
        
        // Obtener información del conjunto
        Conjunto conj = conjuntoRepository.findById(conjId)
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
