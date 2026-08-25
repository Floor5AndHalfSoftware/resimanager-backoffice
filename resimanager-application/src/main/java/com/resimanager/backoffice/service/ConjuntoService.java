package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjuntoId;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PersConjunto;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ConjuntoListResponse;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.ConjuntoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConjuntoService {

    private final ConjuntoRepositoryPort conjuntoRepositoryPort;
    private final PersConjuntoRepositoryPort persConjuntoRepositoryPort;
    private final PerfPersConjuntoRepositoryPort perfPersConjuntoRepositoryPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConjuntoMapper conjuntoMapper;

    @Transactional(readOnly = true)
    public ConjuntoListResponse getConjuntos(String estatus, String search, Integer page, Integer limit) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Conjunto> result =
                conjuntoRepositoryPort.buscarConFiltros(estatusParam, search, page, limit);

        List<ConjuntoDTO> data = result.datos().stream()
                .map(conjuntoMapper::toDTO)
                .toList();

        return ConjuntoListResponse.builder()
                .data(data)
                .total(result.total())
                .page(page)
                .limit(limit)
                .build();
    }

    @Transactional(readOnly = true)
    public ConjuntoDTO getConjuntoById(Integer id) {
        Conjunto conjunto = conjuntoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));
        return conjuntoMapper.toDTO(conjunto);
    }

    @Transactional
    public ConjuntoDTO createConjunto(
            com.resimanager.backoffice.dto.CreateConjuntoRequest request,
            String username, String estacion) {
        Persona contacto = personaRepositoryPort.buscarPorId(request.persContactoId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona contacto no encontrada con ID: " + request.persContactoId()));

        Persona ejecutor = findEjecutor(username);

        Conjunto conj = new Conjunto();
        conj.setConjDocIdent(request.documento());
        conj.setConjNombre(request.nombre());
        conj.setConjTelefono(request.telefono());
        conj.setConjEMail(request.email());
        conj.setConjPersContacto(contacto);
        conj.setConjOrigen("A");
        conj.setConjSts("A");
        conj.setConjUsrCrea(ejecutor);
        conj.setConjFchHorCrea(OffsetDateTime.now());
        conj.setConjEstCrea(estacion);
        conj.setConjUsrMod(ejecutor);
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        return conjuntoMapper.toDTO(conjuntoRepositoryPort.guardar(conj));
    }

    @Transactional
    public ConjuntoDTO updateConjunto(Integer id,
                                       com.resimanager.backoffice.dto.UpdateConjuntoRequest request,
                                       String username, String estacion) {
        Conjunto conj = conjuntoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        if (request.documento() != null) conj.setConjDocIdent(request.documento());
        if (request.nombre() != null) conj.setConjNombre(request.nombre());
        if (request.telefono() != null) conj.setConjTelefono(request.telefono());
        if (request.email() != null) conj.setConjEMail(request.email());
        if (request.persContactoId() != null) {
            Persona contacto = personaRepositoryPort.buscarPorId(request.persContactoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona contacto no encontrada con ID: " + request.persContactoId()));
            conj.setConjPersContacto(contacto);
        }
        if (request.estatus() != null && (request.estatus().equals("A") || request.estatus().equals("I"))) {
            conj.setConjSts(request.estatus());
        }

        conj.setConjUsrMod(findEjecutor(username));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        return conjuntoMapper.toDTO(conjuntoRepositoryPort.guardar(conj));
    }

    @Transactional
    public Map<String, String> deleteConjunto(Integer id, String username, String estacion) {
        Conjunto conj = conjuntoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        conj.setConjSts("I");
        conj.setConjUsrMod(findEjecutor(username));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        conjuntoRepositoryPort.guardar(conj);
        return Map.of("message", "Conjunto inactivado correctamente");
    }

    @Transactional(readOnly = true)
    public ContextoUsuariosResponse getUsuarios(Integer conjId) {
        Conjunto conjunto = conjuntoRepositoryPort.buscarPorId(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        List<PersConjunto> persConjuntos = persConjuntoRepositoryPort.listarActivasPorConjuntoId(conjId);

        List<ContextoUsuariosResponse.UsuarioContextoDTO> usuarios = new ArrayList<>();

        for (PersConjunto pc : persConjuntos) {
            Persona persona = pc.getPcPerid();

            List<PerfPersConjunto> perfiles = perfPersConjuntoRepositoryPort
                    .listarActivasPorConjuntoIdYPersonaId(conjId, persona.getId());

            List<ContextoUsuariosResponse.PerfilSimpleDTO> perfilesDTO = perfiles.stream()
                    .map(ppc -> ContextoUsuariosResponse.PerfilSimpleDTO.builder()
                            .id(ppc.getPpcPrfid().getId())
                            .nombre(ppc.getPpcPrfid().getPrfNombre())
                            .build())
                    .collect(Collectors.toList());

            ContextoUsuariosResponse.PersonaSimpleDTO personaDTO = ContextoUsuariosResponse.PersonaSimpleDTO.builder()
                    .id(persona.getId())
                    .documento(persona.getPerDocIdent())
                    .nombre(persona.getPerNombre())
                    .apellido(persona.getPerApellido())
                    .email(persona.getPerEMail())
                    .telefono(persona.getPerTlfCel())
                    .estatus(persona.getPerSts())
                    .build();

            usuarios.add(ContextoUsuariosResponse.UsuarioContextoDTO.builder()
                    .persona(personaDTO)
                    .perfiles(perfilesDTO)
                    .build());
        }

        return ContextoUsuariosResponse.builder()
                .id(conjunto.getId())
                .nombre(conjunto.getConjNombre())
                .data(usuarios)
                .total((long) usuarios.size())
                .build();
    }

    @Transactional
    public Map<String, Object> asignarPerfiles(Integer conjId, Integer usuarioId,
                                               AsignarPerfilesRequest request,
                                               String username, String estacion) {
        conjuntoRepositoryPort.buscarPorId(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : request.perfiles()) {
            Perfil perfil = perfilRepositoryPort.buscarPorId(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            Optional<PerfPersConjunto> existing =
                    perfPersConjuntoRepositoryPort.buscarPorId(conjId, usuarioId, perfilId);

            if (existing.isPresent()) {
                PerfPersConjunto ppc = existing.get();
                if ("I".equals(ppc.getPPCSts())) {
                    ppc.setPPCSts("A");
                    ppc.setPpcUsrmod(ejecutor);
                    ppc.setPPCFchHorMod(OffsetDateTime.now());
                    ppc.setPPCEstMod(estacion);
                    perfPersConjuntoRepositoryPort.guardar(ppc);
                    reactivados++;
                }
            } else {
                Integer nextPpcid = perfPersConjuntoRepositoryPort.siguienteId();

                PerfPersConjuntoId compositeId = new PerfPersConjuntoId();
                compositeId.setPpcConjid(conjId);
                compositeId.setPpcPerid(usuarioId);
                compositeId.setPpcPrfid(perfilId);

                PerfPersConjunto ppc = new PerfPersConjunto();
                ppc.setId(compositeId);
                ppc.setPpcPrfid(perfil);
                ppc.setPpcid(nextPpcid);
                ppc.setPPCSts("A");
                ppc.setPpcUsrcrea(ejecutor);
                ppc.setPPCFchHorCrea(OffsetDateTime.now());
                ppc.setPPCEstCrea(estacion);
                ppc.setPpcUsrmod(ejecutor);
                ppc.setPPCFchHorMod(OffsetDateTime.now());
                ppc.setPPCEstMod(estacion);

                perfPersConjuntoRepositoryPort.guardar(ppc);
                asignados++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Perfiles asignados correctamente");
        result.put("perfiles_asignados", asignados);
        result.put("perfiles_reactivados", reactivados);
        return result;
    }

    @Transactional
    public Map<String, String> removerPerfil(Integer conjId, Integer usuarioId, Integer perfilId,
                                             String username, String estacion) {
        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        PerfPersConjunto ppc = perfPersConjuntoRepositoryPort.buscarPorId(conjId, usuarioId, perfilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para conjunto ID: " + conjId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        ppc.setPPCSts("I");
        ppc.setPpcUsrmod(ejecutor);
        ppc.setPPCFchHorMod(OffsetDateTime.now());
        ppc.setPPCEstMod(estacion);

        perfPersConjuntoRepositoryPort.guardar(ppc);
        return Map.of("message", "Perfil removido correctamente");
    }

    private Persona findEjecutor(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));
    }
}