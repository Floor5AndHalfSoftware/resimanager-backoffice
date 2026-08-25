package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.dto.UpdateUsuarioRequest;
import com.resimanager.backoffice.dto.UsuarioDTO;
import com.resimanager.backoffice.dto.UsuarioListResponse;
import com.resimanager.backoffice.dto.UsuarioPerfilesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PerfPersAdministradoraRepositoryPort perfPersAdministradoraRepositoryPort;
    private final PerfPersConjuntoRepositoryPort perfPersConjuntoRepositoryPort;
    private final AdministradoraRepositoryPort administradoraRepositoryPort;
    private final ConjuntoRepositoryPort conjuntoRepositoryPort;

    public UsuarioListResponse getUsuarios(String estatus, String search, Integer page, Integer limit) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Persona> result = personaRepositoryPort.buscarConFiltros(estatusParam, searchParam, page, limit);

        return UsuarioListResponse.builder()
                .data(result.datos().stream().map(this::toDTO).toList())
                .total(result.total())
                .page(page)
                .limit(limit)
                .build();
    }

    public UsuarioDTO getUsuarioById(Integer usuarioId) {
        Persona persona = personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return toDTO(persona);
    }

    public UsuarioPerfilesResponse getUsuarioPerfiles(Integer usuarioId) {
        Persona persona = personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<UsuarioPerfilesResponse.ContextoPerfilDTO> contextos = new java.util.ArrayList<>();

        // Perfiles en administradoras
        var ppaList = perfPersAdministradoraRepositoryPort.listarActivasPorPersonaId(usuarioId);
        var ppaByAdm = ppaList.stream()
                .collect(Collectors.groupingBy(ppa -> ppa.getId().getPpaAdmid()));

        if (!ppaByAdm.isEmpty()) {
            List<com.resimanager.backoffice.domain.model.Administradora> admins =
                    administradoraRepositoryPort.buscarActivasPorIds(List.copyOf(ppaByAdm.keySet()));
            for (var adm : admins) {
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
        var ppcList = perfPersConjuntoRepositoryPort.listarActivasPorPersonaId(usuarioId);
        var ppcByConj = ppcList.stream()
                .collect(Collectors.groupingBy(ppc -> ppc.getId().getPpcConjid()));

        if (!ppcByConj.isEmpty()) {
            List<com.resimanager.backoffice.domain.model.Conjunto> conjuntos =
                    conjuntoRepositoryPort.buscarActivosPorIds(List.copyOf(ppcByConj.keySet()));
            for (var conj : conjuntos) {
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
        Persona persona = personaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(executorUsername)
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

        return toDTO(personaRepositoryPort.guardar(persona));
    }

    @Transactional
    public Map<String, String> deleteUsuario(Integer id, String executorUsername, String estacion) {
        Persona persona = personaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        persona.setPerSts("I");
        persona.setPerUsrmod(ejecutor);
        persona.setPerFchHorMod(OffsetDateTime.now());
        persona.setPerEstMod(estacion);

        personaRepositoryPort.guardar(persona);
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