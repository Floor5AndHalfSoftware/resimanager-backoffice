package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.dto.ConjuntoListResponse;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjuntoId;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PersConjunto;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.persistance.repository.ConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PerfPersConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PerfilRepository;
import com.resimanager.backoffice.persistance.repository.PersConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import com.resimanager.backoffice.service.mapper.ConjuntoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private final ConjuntoRepository conjuntoRepository;
    private final PersConjuntoRepository persConjuntoRepository;
    private final PerfPersConjuntoRepository perfPersConjuntoRepository;
    private final PerfilRepository perfilRepository;
    private final PersonaRepository personaRepository;
    private final ConjuntoMapper conjuntoMapper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista conjuntos con filtros opcionales
     */
    @Transactional(readOnly = true)
    public ConjuntoListResponse getConjuntos(String estatus, String search, Integer page, Integer limit) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String estatusParam = (estatus != null && !estatus.isBlank()) ? estatus.trim() : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Conjunto> result = conjuntoRepository.findAllWithFilters(estatusParam, searchParam, pageable);

        List<ConjuntoDTO> data = result.getContent().stream()
                .map(conjuntoMapper::toDTO)
                .toList();

        return ConjuntoListResponse.builder()
                .data(data)
                .total(result.getTotalElements())
                .page(page)
                .limit(limit)
                .build();
    }

    /**
     * Obtiene un conjunto por ID
     * @param id ID del conjunto
     * @return ConjuntoDTO con datos completos
     */
    @Transactional(readOnly = true)
    public ConjuntoDTO getConjuntoById(Integer id) {
        log.debug("Obteniendo conjunto con ID: {}", id);

        Conjunto conjunto = conjuntoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        return conjuntoMapper.toDTO(conjunto);
    }

    /**
     * Crea un nuevo conjunto
     */
    @Transactional
    public ConjuntoDTO createConjunto(
            com.resimanager.backoffice.dto.CreateConjuntoRequest request,
            String username, String estacion) {
        log.info("Creando conjunto: {} por usuario: {}", request.nombre(), username);

        Persona contacto = personaRepository.findById(request.persContactoId())
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

        return conjuntoMapper.toDTO(conjuntoRepository.save(conj));
    }

    /**
     * Actualiza un conjunto existente
     */
    @Transactional
    public ConjuntoDTO updateConjunto(Integer id,
                                       com.resimanager.backoffice.dto.UpdateConjuntoRequest request,
                                       String username, String estacion) {
        log.info("Actualizando conjunto ID: {} por usuario: {}", id, username);

        Conjunto conj = conjuntoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        if (request.documento() != null) conj.setConjDocIdent(request.documento());
        if (request.nombre() != null) conj.setConjNombre(request.nombre());
        if (request.telefono() != null) conj.setConjTelefono(request.telefono());
        if (request.email() != null) conj.setConjEMail(request.email());
        if (request.persContactoId() != null) {
            Persona contacto = personaRepository.findById(request.persContactoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona contacto no encontrada con ID: " + request.persContactoId()));
            conj.setConjPersContacto(contacto);
        }
        if (request.estatus() != null && (request.estatus().equals("A") || request.estatus().equals("I"))) {
            conj.setConjSts(request.estatus());
        }

        conj.setConjUsrMod(findEjecutor(username));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        return conjuntoMapper.toDTO(conjuntoRepository.save(conj));
    }

    /**
     * Inactiva (soft-delete) un conjunto
     */
    @Transactional
    public Map<String, String> deleteConjunto(Integer id, String username, String estacion) {
        log.info("Inactivando conjunto ID: {} por usuario: {}", id, username);

        Conjunto conj = conjuntoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        conj.setConjSts("I");
        conj.setConjUsrMod(findEjecutor(username));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        conjuntoRepository.save(conj);
        return Map.of("message", "Conjunto inactivado correctamente");
    }

    /**
     * Obtiene la lista de usuarios activos de un conjunto con sus perfiles
     * @param conjId ID del conjunto
     * @return ContextoUsuariosResponse con los usuarios y sus perfiles
     */
    @Transactional(readOnly = true)
    public ContextoUsuariosResponse getUsuarios(Integer conjId) {
        log.debug("Obteniendo usuarios del conjunto ID: {}", conjId);

        Conjunto conjunto = conjuntoRepository.findById(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        List<PersConjunto> persConjuntos = persConjuntoRepository.findActiveByConjId(conjId);

        List<ContextoUsuariosResponse.UsuarioContextoDTO> usuarios = new ArrayList<>();

        for (PersConjunto pc : persConjuntos) {
            Persona persona = pc.getPcPerid();

            List<PerfPersConjunto> perfiles = perfPersConjuntoRepository
                    .findActiveByConjIdAndPerId(conjId, persona.getId());

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

    /**
     * Asigna perfiles a un usuario en un conjunto.
     * Si el registro ya existe pero está inactivo, lo reactiva.
     * @param conjId ID del conjunto
     * @param usuarioId ID del usuario (persona)
     * @param request Lista de IDs de perfiles a asignar
     * @param username Usuario que realiza la operación
     * @param estacion Estación desde donde se realiza la operación
     * @return Mensaje de resultado
     */
    @Transactional
    public Map<String, Object> asignarPerfiles(Integer conjId, Integer usuarioId,
                                               AsignarPerfilesRequest request,
                                               String username, String estacion) {
        log.info("Asignando perfiles al usuario ID: {} en conjunto ID: {} por usuario: {}",
                usuarioId, conjId, username);

        conjuntoRepository.findById(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        personaRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutor = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : request.perfiles()) {
            Perfil perfil = perfilRepository.findById(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            PerfPersConjuntoId compositeId = new PerfPersConjuntoId();
            compositeId.setPpcConjid(conjId);
            compositeId.setPpcPerid(usuarioId);
            compositeId.setPpcPrfid(perfilId);

            Optional<PerfPersConjunto> existing = perfPersConjuntoRepository.findById(compositeId);

            if (existing.isPresent()) {
                PerfPersConjunto ppc = existing.get();
                if ("I".equals(ppc.getPPCSts())) {
                    ppc.setPPCSts("A");
                    ppc.setPpcUsrmod(ejecutor);
                    ppc.setPPCFchHorMod(OffsetDateTime.now());
                    ppc.setPPCEstMod(estacion);
                    perfPersConjuntoRepository.save(ppc);
                    reactivados++;
                }
            } else {
                Integer nextPpcid = queryNextPpcid();

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

                perfPersConjuntoRepository.save(ppc);
                asignados++;
            }
        }

        log.info("Perfiles procesados para usuario ID: {} en conjunto ID: {} - nuevos: {}, reactivados: {}",
                usuarioId, conjId, asignados, reactivados);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Perfiles asignados correctamente");
        result.put("perfiles_asignados", asignados);
        result.put("perfiles_reactivados", reactivados);
        return result;
    }

    /**
     * Remueve (inactiva) un perfil de un usuario en un conjunto
     * @param conjId ID del conjunto
     * @param usuarioId ID del usuario (persona)
     * @param perfilId ID del perfil a remover
     * @param username Usuario que realiza la operación
     * @param estacion Estación desde donde se realiza la operación
     * @return Mensaje de resultado
     */
    @Transactional
    public Map<String, String> removerPerfil(Integer conjId, Integer usuarioId, Integer perfilId,
                                             String username, String estacion) {
        log.info("Removiendo perfil ID: {} del usuario ID: {} en conjunto ID: {} por usuario: {}",
                perfilId, usuarioId, conjId, username);

        Persona ejecutor = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        PerfPersConjuntoId compositeId = new PerfPersConjuntoId();
        compositeId.setPpcConjid(conjId);
        compositeId.setPpcPerid(usuarioId);
        compositeId.setPpcPrfid(perfilId);

        PerfPersConjunto ppc = perfPersConjuntoRepository.findById(compositeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para conjunto ID: " + conjId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        ppc.setPPCSts("I");
        ppc.setPpcUsrmod(ejecutor);
        ppc.setPPCFchHorMod(OffsetDateTime.now());
        ppc.setPPCEstMod(estacion);

        perfPersConjuntoRepository.save(ppc);

        log.info("Perfil ID: {} removido del usuario ID: {} en conjunto ID: {}", perfilId, usuarioId, conjId);
        return Map.of("message", "Perfil removido correctamente");
    }

    private Persona findEjecutor(String username) {
        return personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));
    }

    /**
     * Obtiene el siguiente valor para el campo ppcid usando MAX+1 sobre la tabla
     */
    private Integer queryNextPpcid() {
        Number maxId = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(ppcid), 0) + 1 FROM \"PerfPersConjunto\"")
                .getSingleResult();
        return maxId.intValue();
    }
}
