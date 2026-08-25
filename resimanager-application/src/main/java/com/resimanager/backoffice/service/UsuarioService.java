package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.UpdateUsuarioRequest;
import com.resimanager.backoffice.dto.UsuarioDTO;
import com.resimanager.backoffice.dto.UsuarioListResponse;
import com.resimanager.backoffice.dto.UsuarioPerfilesResponse;
import com.resimanager.backoffice.persistance.entity.Administradora;
import com.resimanager.backoffice.persistance.entity.Conjunto;
import com.resimanager.backoffice.persistance.entity.PerfPersAdministradora;
import com.resimanager.backoffice.persistance.entity.PerfPersConjunto;
import com.resimanager.backoffice.persistance.entity.Persona;
import com.resimanager.backoffice.persistance.repository.AdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.ConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PerfPersAdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.PerfPersConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final PersonaRepository personaRepository;
    private final PerfPersAdministradoraRepository perfPersAdministradoraRepository;
    private final PerfPersConjuntoRepository perfPersConjuntoRepository;
    private final AdministradoraRepository administradoraRepository;
    private final ConjuntoRepository conjuntoRepository;

    public UsuarioListResponse getUsuarios(String estatus, String search, Integer page, Integer limit) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String estatusParam = (estatus != null && !estatus.isBlank()) ? estatus.trim() : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Persona> result = personaRepository.findAllWithFilters(estatusParam, searchParam, pageable);

        return UsuarioListResponse.builder()
                .data(result.getContent().stream().map(this::toDTO).toList())
                .total(result.getTotalElements())
                .page(page)
                .limit(limit)
                .build();
    }

    public UsuarioDTO getUsuarioById(Integer usuarioId) {
        Persona persona = personaRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return toDTO(persona);
    }

    public UsuarioPerfilesResponse getUsuarioPerfiles(Integer usuarioId) {
        Persona persona = personaRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Perfiles en administradoras
        List<PerfPersAdministradora> ppaList = perfPersAdministradoraRepository.findActiveByPersonaId(usuarioId);
        Map<Integer, List<PerfPersAdministradora>> ppaByAdm = ppaList.stream()
                .collect(Collectors.groupingBy(ppa -> ppa.getId().getPpaAdmid()));

        List<UsuarioPerfilesResponse.ContextoPerfilDTO> contextos = new java.util.ArrayList<>();

        if (!ppaByAdm.isEmpty()) {
            List<Administradora> admins = administradoraRepository.findActiveByIds(List.copyOf(ppaByAdm.keySet()));
            for (Administradora adm : admins) {
                List<UsuarioPerfilesResponse.PerfilSimpleDTO> perfiles = ppaByAdm.get(adm.getId()).stream()
                        .map(ppa -> UsuarioPerfilesResponse.PerfilSimpleDTO.builder()
                                .id(ppa.getPpaPrfid().getId())
                                .nombre(ppa.getPpaPrfid().getPrfNombre())
                                .build())
                        .toList();
                contextos.add(UsuarioPerfilesResponse.ContextoPerfilDTO.builder()
                        .tipo("ADMINISTRADORA")
                        .entidad(UsuarioPerfilesResponse.EntidadDTO.builder()
                                .id(adm.getId()).nombre(adm.getAdmNombre()).build())
                        .perfiles(perfiles)
                        .build());
            }
        }

        // Perfiles en conjuntos
        List<PerfPersConjunto> ppcList = perfPersConjuntoRepository.findActiveByPersonaId(usuarioId);
        Map<Integer, List<PerfPersConjunto>> ppcByConj = ppcList.stream()
                .collect(Collectors.groupingBy(ppc -> ppc.getId().getPpcConjid()));

        if (!ppcByConj.isEmpty()) {
            List<Conjunto> conjuntos = conjuntoRepository.findActiveByIds(List.copyOf(ppcByConj.keySet()));
            for (Conjunto conj : conjuntos) {
                List<UsuarioPerfilesResponse.PerfilSimpleDTO> perfiles = ppcByConj.get(conj.getId()).stream()
                        .map(ppc -> UsuarioPerfilesResponse.PerfilSimpleDTO.builder()
                                .id(ppc.getPpcPrfid().getId())
                                .nombre(ppc.getPpcPrfid().getPrfNombre())
                                .build())
                        .toList();
                contextos.add(UsuarioPerfilesResponse.ContextoPerfilDTO.builder()
                        .tipo("CONJUNTO")
                        .entidad(UsuarioPerfilesResponse.EntidadDTO.builder()
                                .id(conj.getId()).nombre(conj.getConjNombre()).build())
                        .perfiles(perfiles)
                        .build());
            }
        }

        return UsuarioPerfilesResponse.builder()
                .persona(toDTO(persona))
                .contextos(contextos)
                .build();
    }

    @Transactional
    public UsuarioDTO updateUsuario(Integer id, UpdateUsuarioRequest request, String executorUsername, String estacion) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutor = personaRepository.findByPerUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        if (request.nombre() != null && !request.nombre().isBlank()) {
            persona.setPerNombre(request.nombre().trim());
        }
        if (request.apellido() != null && !request.apellido().isBlank()) {
            persona.setPerApellido(request.apellido().trim());
        }
        if (request.telefono() != null && !request.telefono().isBlank()) {
            persona.setPerTlfCel(request.telefono().trim());
        }
        if (request.email() != null && !request.email().isBlank()) {
            persona.setPerEMail(request.email().trim());
        }
        if (request.estatus() != null && (request.estatus().equals("A") || request.estatus().equals("I"))) {
            persona.setPerSts(request.estatus());
        }

        persona.setPerUsrmod(ejecutor);
        persona.setPerFchHorMod(OffsetDateTime.now());
        persona.setPerEstMod(estacion);

        return toDTO(personaRepository.save(persona));
    }

    @Transactional
    public Map<String, String> deleteUsuario(Integer id, String executorUsername, String estacion) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutor = personaRepository.findByPerUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        persona.setPerSts("I");
        persona.setPerUsrmod(ejecutor);
        persona.setPerFchHorMod(OffsetDateTime.now());
        persona.setPerEstMod(estacion);

        personaRepository.save(persona);
        return Map.of("message", "Usuario inactivado correctamente");
    }

    private UsuarioDTO toDTO(Persona p) {
        return UsuarioDTO.builder()
                .id(p.getId())
                .documento(p.getPerDocIdent())
                .nombre(p.getPerNombre())
                .apellido(p.getPerApellido())
                .email(p.getPerEMail())
                .telefono(p.getPerTlfCel())
                .usuario(p.getPerUsuario())
                .estatus(p.getPerSts())
                .build();
    }
}
