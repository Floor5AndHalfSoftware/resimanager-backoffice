package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.dto.AdministradoraListResponse;
import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.persistance.entity.Administradora;
import com.resimanager.backoffice.persistance.entity.PerfPersAdministradora;
import com.resimanager.backoffice.persistance.entity.PerfPersAdministradoraId;
import com.resimanager.backoffice.persistance.entity.Perfil;
import com.resimanager.backoffice.persistance.entity.PersAdministradora;
import com.resimanager.backoffice.persistance.entity.Persona;
import com.resimanager.backoffice.persistance.repository.AdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.PerfPersAdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.PerfilRepository;
import com.resimanager.backoffice.persistance.repository.PersAdministradoraRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
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
public class AdministradoraService {

    private final AdministradoraRepository administradoraRepository;
    private final PersAdministradoraRepository persAdministradoraRepository;
    private final PerfPersAdministradoraRepository perfPersAdministradoraRepository;
    private final PerfilRepository perfilRepository;
    private final PersonaRepository personaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista administradoras con filtros opcionales
     */
    @Transactional(readOnly = true)
    public AdministradoraListResponse getAdministradoras(String estatus, String search, Integer page, Integer limit) {
        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String estatusParam = (estatus != null && !estatus.isBlank()) ? estatus.trim() : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Administradora> result = administradoraRepository.findAllWithFilters(estatusParam, searchParam, pageable);

        List<AdministradoraDTO> data = result.getContent().stream()
                .map(a -> AdministradoraDTO.builder()
                        .id(a.getId())
                        .nombre(a.getAdmNombre())
                        .documento(a.getAdmDocIdent())
                        .email(a.getAdmEMail())
                        .telefono(a.getAdmTelefono())
                        .estatus(a.getAdmSts())
                        .build())
                .toList();

        return AdministradoraListResponse.builder()
                .data(data)
                .total(result.getTotalElements())
                .page(page)
                .limit(limit)
                .build();
    }

    /**
     * Obtiene una administradora por ID
     * @param id ID de la administradora
     * @return Map con id y nombre
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdministradoraById(Integer id) {
        log.debug("Obteniendo administradora con ID: {}", id);

        Administradora administradora = administradoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));

        Map<String, Object> result = new HashMap<>();
        result.put("id", administradora.getId());
        result.put("nombre", administradora.getAdmNombre());
        return result;
    }

    /**
     * Obtiene la lista de usuarios activos de una administradora con sus perfiles
     * @param admId ID de la administradora
     * @return ContextoUsuariosResponse con los usuarios y sus perfiles
     */
    @Transactional(readOnly = true)
    public ContextoUsuariosResponse getUsuarios(Integer admId) {
        log.debug("Obteniendo usuarios de la administradora ID: {}", admId);

        Administradora administradora = administradoraRepository.findById(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        List<PersAdministradora> persAdministradoras = persAdministradoraRepository.findActiveByAdmId(admId);

        List<ContextoUsuariosResponse.UsuarioContextoDTO> usuarios = new ArrayList<>();

        for (PersAdministradora pa : persAdministradoras) {
            Persona persona = pa.getPaPerid();

            List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepository
                    .findActiveByAdmIdAndPerId(admId, persona.getId());

            List<ContextoUsuariosResponse.PerfilSimpleDTO> perfilesDTO = perfiles.stream()
                    .map(ppa -> ContextoUsuariosResponse.PerfilSimpleDTO.builder()
                            .id(ppa.getPpaPrfid().getId())
                            .nombre(ppa.getPpaPrfid().getPrfNombre())
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
                .id(administradora.getId())
                .nombre(administradora.getAdmNombre())
                .data(usuarios)
                .total((long) usuarios.size())
                .build();
    }

    /**
     * Asigna perfiles a un usuario en una administradora.
     * Si el registro ya existe pero está inactivo, lo reactiva.
     * @param admId ID de la administradora
     * @param usuarioId ID del usuario (persona)
     * @param request Lista de IDs de perfiles a asignar
     * @param username Usuario que realiza la operación
     * @param estacion Estación desde donde se realiza la operación
     * @return Mensaje de resultado
     */
    @Transactional
    public Map<String, Object> asignarPerfiles(Integer admId, Integer usuarioId,
                                               AsignarPerfilesRequest request,
                                               String username, String estacion) {
        log.info("Asignando perfiles al usuario ID: {} en administradora ID: {} por usuario: {}",
                usuarioId, admId, username);

        administradoraRepository.findById(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        Persona usuarioTarget = personaRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutor = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : request.getPerfiles()) {
            Perfil perfil = perfilRepository.findById(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            PerfPersAdministradoraId compositeId = new PerfPersAdministradoraId();
            compositeId.setPpaAdmid(admId);
            compositeId.setPpaPerid(usuarioId);
            compositeId.setPpaPrfid(perfilId);

            Optional<PerfPersAdministradora> existing = perfPersAdministradoraRepository.findById(compositeId);

            if (existing.isPresent()) {
                PerfPersAdministradora ppa = existing.get();
                if ("I".equals(ppa.getPPASts())) {
                    ppa.setPPASts("A");
                    ppa.setPpaUsrmod(ejecutor);
                    ppa.setPPAFchHorMod(OffsetDateTime.now());
                    ppa.setPPAEstMod(estacion);
                    perfPersAdministradoraRepository.save(ppa);
                    reactivados++;
                }
            } else {
                Integer nextPpaid = queryNextPpaid();

                PerfPersAdministradora ppa = new PerfPersAdministradora();
                ppa.setId(compositeId);
                ppa.setPpaPrfid(perfil);
                ppa.setPpaid(nextPpaid);
                ppa.setPPASts("A");
                ppa.setPpaUsrcrea(ejecutor);
                ppa.setPPAFchHorCrea(OffsetDateTime.now());
                ppa.setPPAEstCrea(estacion);
                ppa.setPpaUsrmod(ejecutor);
                ppa.setPPAFchHorMod(OffsetDateTime.now());
                ppa.setPPAEstMod(estacion);

                perfPersAdministradoraRepository.save(ppa);
                asignados++;
            }
        }

        log.info("Perfiles procesados para usuario ID: {} en administradora ID: {} - nuevos: {}, reactivados: {}",
                usuarioId, admId, asignados, reactivados);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Perfiles asignados correctamente");
        result.put("perfiles_asignados", asignados);
        result.put("perfiles_reactivados", reactivados);
        return result;
    }

    /**
     * Remueve (inactiva) un perfil de un usuario en una administradora
     * @param admId ID de la administradora
     * @param usuarioId ID del usuario (persona)
     * @param perfilId ID del perfil a remover
     * @param username Usuario que realiza la operación
     * @param estacion Estación desde donde se realiza la operación
     * @return Mensaje de resultado
     */
    @Transactional
    public Map<String, String> removerPerfil(Integer admId, Integer usuarioId, Integer perfilId,
                                             String username, String estacion) {
        log.info("Removiendo perfil ID: {} del usuario ID: {} en administradora ID: {} por usuario: {}",
                perfilId, usuarioId, admId, username);

        Persona ejecutor = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        PerfPersAdministradoraId compositeId = new PerfPersAdministradoraId();
        compositeId.setPpaAdmid(admId);
        compositeId.setPpaPerid(usuarioId);
        compositeId.setPpaPrfid(perfilId);

        PerfPersAdministradora ppa = perfPersAdministradoraRepository.findById(compositeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para administradora ID: " + admId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        ppa.setPPASts("I");
        ppa.setPpaUsrmod(ejecutor);
        ppa.setPPAFchHorMod(OffsetDateTime.now());
        ppa.setPPAEstMod(estacion);

        perfPersAdministradoraRepository.save(ppa);

        log.info("Perfil ID: {} removido del usuario ID: {} en administradora ID: {}", perfilId, usuarioId, admId);
        return Map.of("message", "Perfil removido correctamente");
    }

    /**
     * Obtiene el siguiente valor para el campo ppaid usando MAX+1 sobre la tabla
     */
    private Integer queryNextPpaid() {
        Number maxId = (Number) entityManager
                .createNativeQuery("SELECT COALESCE(MAX(ppaid), 0) + 1 FROM \"PerfPersAdministradora\"")
                .getSingleResult();
        return maxId.intValue();
    }
}
