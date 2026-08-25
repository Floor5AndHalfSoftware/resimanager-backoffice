package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.PerfPersAdministradora;
import com.resimanager.backoffice.domain.model.PerfPersAdministradoraId;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PersAdministradora;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.dto.AdministradoraListResponse;
import com.resimanager.backoffice.dto.AsignarPerfilesRequest;
import com.resimanager.backoffice.dto.ContextoUsuariosResponse;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.AdministradoraMapper;
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
public class AdministradoraService {

    private final AdministradoraRepositoryPort administradoraRepositoryPort;
    private final PersAdministradoraRepositoryPort persAdministradoraRepositoryPort;
    private final PerfPersAdministradoraRepositoryPort perfPersAdministradoraRepositoryPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final AdministradoraMapper administradoraMapper;

    @Transactional(readOnly = true)
    public AdministradoraListResponse getAdministradoras(String estatus, String search, Integer page, Integer limit) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Administradora> result =
                administradoraRepositoryPort.buscarConFiltros(estatusParam, search, page, limit);

        List<AdministradoraDTO> data = result.datos().stream()
                .map(administradoraMapper::toDTO)
                .toList();

        return AdministradoraListResponse.builder()
                .data(data)
                .total(result.total())
                .page(page)
                .limit(limit)
                .build();
    }

    @Transactional(readOnly = true)
    public AdministradoraDTO getAdministradoraById(Integer id) {
        Administradora administradora = administradoraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));
        return administradoraMapper.toDTO(administradora);
    }

    @Transactional
    public AdministradoraDTO createAdministradora(
            com.resimanager.backoffice.dto.CreateAdministradoraRequest request,
            String username, String estacion) {
        Persona ejecutor = findEjecutor(username);

        Administradora adm = new Administradora();
        adm.setId(administradoraRepositoryPort.siguienteId());
        adm.setAdmDocIdent(request.documento());
        adm.setAdmNombre(request.nombre());
        adm.setAdmTelefono(request.telefono());
        adm.setAdmEMail(request.email());
        adm.setAdmSts("A");
        adm.setAdmPersContacto(ejecutor);
        adm.setAdmUsrCrea(ejecutor);
        adm.setAdmFchHorCrea(OffsetDateTime.now());
        adm.setAdmEstCrea(estacion);
        adm.setAdmUsrMod(ejecutor);
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        return administradoraMapper.toDTO(administradoraRepositoryPort.guardar(adm));
    }

    @Transactional
    public AdministradoraDTO updateAdministradora(Integer id,
                                                   com.resimanager.backoffice.dto.UpdateAdministradoraRequest request,
                                                   String username, String estacion) {
        Administradora adm = administradoraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));

        if (request.documento() != null) adm.setAdmDocIdent(request.documento());
        if (request.nombre() != null) adm.setAdmNombre(request.nombre());
        if (request.telefono() != null) adm.setAdmTelefono(request.telefono());
        if (request.email() != null) adm.setAdmEMail(request.email());
        if (request.estatus() != null && (request.estatus().equals("A") || request.estatus().equals("I"))) {
            adm.setAdmSts(request.estatus());
        }

        adm.setAdmUsrMod(findEjecutor(username));
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        return administradoraMapper.toDTO(administradoraRepositoryPort.guardar(adm));
    }

    @Transactional
    public Map<String, String> deleteAdministradora(Integer id, String username, String estacion) {
        Administradora adm = administradoraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));

        adm.setAdmSts("I");
        adm.setAdmUsrMod(findEjecutor(username));
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        administradoraRepositoryPort.guardar(adm);
        return Map.of("message", "Administradora inactivada correctamente");
    }

    @Transactional(readOnly = true)
    public ContextoUsuariosResponse getUsuarios(Integer admId) {
        Administradora administradora = administradoraRepositoryPort.buscarPorId(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        List<PersAdministradora> persAdministradoras = persAdministradoraRepositoryPort.listarActivasPorAdministradoraId(admId);

        List<ContextoUsuariosResponse.UsuarioContextoDTO> usuarios = new ArrayList<>();

        for (PersAdministradora pa : persAdministradoras) {
            Persona persona = pa.getPaPerid();

            List<PerfPersAdministradora> perfiles = perfPersAdministradoraRepositoryPort
                    .listarActivasPorAdministradoraIdYPersonaId(admId, persona.getId());

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

    @Transactional
    public Map<String, Object> asignarPerfiles(Integer admId, Integer usuarioId,
                                                AsignarPerfilesRequest request,
                                                String username, String estacion) {
        administradoraRepositoryPort.buscarPorId(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : request.perfiles()) {
            Perfil perfil = perfilRepositoryPort.buscarPorId(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            Optional<PerfPersAdministradora> existing =
                    perfPersAdministradoraRepositoryPort.buscarPorId(admId, usuarioId, perfilId);

            if (existing.isPresent()) {
                PerfPersAdministradora ppa = existing.get();
                if ("I".equals(ppa.getPPASts())) {
                    ppa.setPPASts("A");
                    ppa.setPpaUsrmod(ejecutor);
                    ppa.setPPAFchHorMod(OffsetDateTime.now());
                    ppa.setPPAEstMod(estacion);
                    perfPersAdministradoraRepositoryPort.guardar(ppa);
                    reactivados++;
                }
            } else {
                Integer nextPpaid = perfPersAdministradoraRepositoryPort.siguienteId();

                PerfPersAdministradoraId compositeId = new PerfPersAdministradoraId();
                compositeId.setPpaAdmid(admId);
                compositeId.setPpaPerid(usuarioId);
                compositeId.setPpaPrfid(perfilId);

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

                perfPersAdministradoraRepositoryPort.guardar(ppa);
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
    public Map<String, String> removerPerfil(Integer admId, Integer usuarioId, Integer perfilId,
                                              String username, String estacion) {
        personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));

        PerfPersAdministradora ppa = perfPersAdministradoraRepositoryPort.buscarPorId(admId, usuarioId, perfilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para administradora ID: " + admId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        Persona ejecutor = personaRepositoryPort.buscarPorUsuario(username).orElseThrow();
        ppa.setPPASts("I");
        ppa.setPpaUsrmod(ejecutor);
        ppa.setPPAFchHorMod(OffsetDateTime.now());
        ppa.setPPAEstMod(estacion);

        perfPersAdministradoraRepositoryPort.guardar(ppa);
        return Map.of("message", "Perfil removido correctamente");
    }

    private Persona findEjecutor(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));
    }
}