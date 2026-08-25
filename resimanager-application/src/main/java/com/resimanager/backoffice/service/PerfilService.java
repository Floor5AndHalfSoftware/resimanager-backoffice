package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.ModPerfil;
import com.resimanager.backoffice.domain.model.ModPerfilId;
import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PermisoModulo;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.AccOpcPerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ModPerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ModuloRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.dto.AsignarModulosRequest;
import com.resimanager.backoffice.dto.CreatePerfilRequest;
import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.dto.PerfilDTO;
import com.resimanager.backoffice.dto.PerfilDetalleDTO;
import com.resimanager.backoffice.dto.PerfilListResponse;
import com.resimanager.backoffice.dto.PermisoDTO;
import com.resimanager.backoffice.dto.UpdatePerfilRequest;
import com.resimanager.backoffice.exception.BadRequestException;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.service.mapper.ModuloMapper;
import com.resimanager.backoffice.service.mapper.PerfilMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilService {

    private final PerfilRepositoryPort perfilRepositoryPort;
    private final ModuloRepositoryPort moduloRepositoryPort;
    private final ModPerfilRepositoryPort modPerfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final AccOpcPerfilRepositoryPort accOpcPerfilRepositoryPort;
    private final PerfilMapper perfilMapper;
    private final ModuloMapper moduloMapper;

    @Transactional(readOnly = true)
    public PerfilListResponse getPerfiles(String estatus, Integer nivel, String search, Integer page, Integer limit) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Perfil> perfilesPage = perfilRepositoryPort.buscarConFiltros(estatusParam, nivel, search,
                page != null && page > 0 ? page : 1, limit != null && limit > 0 ? limit : 25);

        List<PerfilDTO> perfilesDTO = perfilesPage.datos().stream()
                .map(perfil -> {
                    Long usuariosAsignados = perfilRepositoryPort.contarUsuariosAsignados(perfil.getId());
                    PerfilDTO base = perfilMapper.toDTO(perfil);
                    return base.toBuilder().usuariosAsignados(usuariosAsignados).build();
                })
                .collect(Collectors.toList());

        return PerfilListResponse.builder()
                .data(perfilesDTO)
                .total(perfilesPage.total())
                .page(perfilesPage.pagina())
                .limit(perfilesPage.limite())
                .build();
    }

    @Transactional(readOnly = true)
    public PerfilDetalleDTO getPerfilById(Integer id) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        List<Modulo> modulos = moduloRepositoryPort.listarPorPerfilId(id);
        List<ModuloDTO> modulosDTO = modulos.stream()
                .map(moduloMapper::toDTO)
                .collect(Collectors.toList());

        List<PermisoModulo> permisos = accOpcPerfilRepositoryPort.listarPermisosPorPerfilId(id);
        List<PermisoDTO> permisosDTO = groupPermissionsByModule(permisos);

        Long usuariosAsignados = perfilRepositoryPort.contarUsuariosAsignados(id);

        return PerfilDetalleDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getPrfNombre())
                .descripcion(perfil.getPrfDescrip())
                .estatus(perfil.getPrfSts())
                .nivel(perfil.getPrfNivel())
                .modulos(modulosDTO)
                .permisos(permisosDTO)
                .usuariosAsignados(usuariosAsignados)
                .fechaCreacion(perfil.getPrfFchHorCrea())
                .build();
    }

    @Transactional
    public PerfilDTO createPerfil(CreatePerfilRequest request, String username, String estacion) {
        if (perfilRepositoryPort.existePorNombre(request.nombre())) {
            throw new BadRequestException("Ya existe un perfil con el nombre: " + request.nombre());
        }

        Persona usuario = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        Perfil perfil = new Perfil();
        perfil.setPrfNombre(request.nombre());
        perfil.setPrfDescrip(request.descripcion());
        perfil.setPrfNivel(request.nivel());
        perfil.setPrfSts("A");
        perfil.setPrfUsrcrea(usuario);
        perfil.setPrfFchHorCrea(OffsetDateTime.now());
        perfil.setPrfEstCrea(estacion);
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);

        perfil = perfilRepositoryPort.guardar(perfil);
        return perfilMapper.toDTO(perfil);
    }

    @Transactional
    public PerfilDTO updatePerfil(Integer id, UpdatePerfilRequest request, String username, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        boolean updated = false;

        if (request.nombre() != null && !request.nombre().equals(perfil.getPrfNombre())) {
            if (perfilRepositoryPort.existePorNombreYDistintoId(request.nombre(), id)) {
                throw new BadRequestException("Ya existe otro perfil con el nombre: " + request.nombre());
            }
            perfil.setPrfNombre(request.nombre());
            updated = true;
        }

        if (request.descripcion() != null) {
            perfil.setPrfDescrip(request.descripcion());
            updated = true;
        }

        if (request.estatus() != null && !request.estatus().equals(perfil.getPrfSts())) {
            perfil.setPrfSts(request.estatus());
            updated = true;
        }

        if (request.nivel() != null && !request.nivel().equals(perfil.getPrfNivel())) {
            perfil.setPrfNivel(request.nivel());
            updated = true;
        }

        if (updated) {
            perfil.setPrfUsrmod(usuario);
            perfil.setPrfFchHorMod(OffsetDateTime.now());
            perfil.setPrfEstMod(estacion);
            perfil = perfilRepositoryPort.guardar(perfil);
        }

        return perfilMapper.toDTO(perfil);
    }

    @Transactional
    public Map<String, String> deletePerfil(Integer id, String username, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        perfil.setPrfSts("I");
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);

        perfilRepositoryPort.guardar(perfil);
        return Map.of("message", "Perfil inactivado correctamente");
    }

    @Transactional
    public Map<String, Object> asignarModulos(Integer id, AsignarModulosRequest request, String username, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        List<Modulo> modulos = moduloRepositoryPort.buscarActivosPorIds(request.modulos());
        if (modulos.size() != request.modulos().size()) {
            throw new BadRequestException("Uno o más módulos no existen o están inactivos");
        }

        int asignados = 0;
        for (Modulo modulo : modulos) {
            ModPerfil existing = modPerfilRepositoryPort.buscarPorId(id, modulo.getModId()).orElse(null);

            if (existing != null) {
                if ("I".equals(existing.getMPSts())) {
                    existing.setMPSts("A");
                    existing.setMpUsrmod(usuario);
                    existing.setMPFchHorMod(OffsetDateTime.now());
                    existing.setMPEstMod(estacion);
                    modPerfilRepositoryPort.guardar(existing);
                    asignados++;
                }
            } else {
                ModPerfil modPerfil = new ModPerfil();

                ModPerfilId modPerfilId = new ModPerfilId();
                modPerfilId.setMpPrfid(id);
                modPerfilId.setMpModid(modulo.getModId());
                modPerfil.setId(modPerfilId);

                modPerfil.setMpPrfid(perfil);
                modPerfil.setMpModid(modulo);
                modPerfil.setMpid(modulo.getModId());
                modPerfil.setMPSts("A");
                modPerfil.setMpUsrcrea(usuario);
                modPerfil.setMPFchHorCrea(OffsetDateTime.now());
                modPerfil.setMPEstCrea(estacion);
                modPerfil.setMpUsrmod(usuario);
                modPerfil.setMPFchHorMod(OffsetDateTime.now());
                modPerfil.setMPEstMod(estacion);

                modPerfilRepositoryPort.guardar(modPerfil);
                asignados++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Módulos asignados correctamente");
        result.put("modulos_asignados", asignados);
        return result;
    }

    @Transactional
    public Map<String, String> revocarModulo(Integer perfilId, Integer moduloId, String username, String estacion) {
        if (perfilRepositoryPort.buscarPorId(perfilId).isEmpty()) {
            throw new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId);
        }

        if (!moduloRepositoryPort.existePorId(moduloId)) {
            throw new ResourceNotFoundException("Módulo no encontrado con ID: " + moduloId);
        }

        modPerfilRepositoryPort.eliminar(perfilId, moduloId);
        return Map.of("message", "Módulo removido del perfil");
    }

    private List<PermisoDTO> groupPermissionsByModule(List<PermisoModulo> permisos) {
        Map<Integer, PermisoDTO> permisosMap = new HashMap<>();

        for (PermisoModulo permiso : permisos) {
            permisosMap.merge(permiso.moduloId(),
                new PermisoDTO(permiso.moduloId(), permiso.moduloNombre(), new ArrayList<>(List.of(permiso.accionNombre()))),
                (existing, ignored) -> {
                    existing.acciones().add(permiso.accionNombre());
                    return existing;
                });
        }

        return new ArrayList<>(permisosMap.values());
    }
}