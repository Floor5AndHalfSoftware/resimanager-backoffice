package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.*;
import com.resimanager.backoffice.exception.BadRequestException;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import com.resimanager.backoffice.persistance.entity.ModPerfil;
import com.resimanager.backoffice.persistance.entity.ModPerfilId;
import com.resimanager.backoffice.persistance.entity.Modulo;
import com.resimanager.backoffice.persistance.entity.Perfil;
import com.resimanager.backoffice.persistance.entity.Persona;
import com.resimanager.backoffice.persistance.repository.ModPerfilRepository;
import com.resimanager.backoffice.persistance.repository.ModuloRepository;
import com.resimanager.backoffice.persistance.repository.PerfilRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final PerfilRepository perfilRepository;
    private final ModuloRepository moduloRepository;
    private final ModPerfilRepository modPerfilRepository;
    private final PersonaRepository personaRepository;

    /**
     * Lista perfiles con filtros y paginación
     * @param estatus Filtro por estatus (opcional)
     * @param nivel Filtro por nivel (opcional)
     * @param search Búsqueda por nombre/descripción (opcional)
     * @param page Número de página (default 1)
     * @param limit Registros por página (default 25)
     * @return Lista paginada de perfiles
     */
    @Transactional(readOnly = true)
    public PerfilListResponse getPerfiles(String estatus, Integer nivel, String search, Integer page, Integer limit) {
        log.debug("Listando perfiles - estatus: {}, nivel: {}, search: {}, page: {}, limit: {}", 
                  estatus, nivel, search, page, limit);
        
        // Validar y ajustar parámetros de paginación
        int pageNumber = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 25;
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        
        // Buscar perfiles con filtros
        Page<Perfil> perfilesPage = perfilRepository.findWithFilters(estatus, nivel, search, pageable);
        
        // Convertir a DTOs
        List<PerfilDTO> perfilesDTO = perfilesPage.getContent().stream()
                .map(this::toPerfilDTO)
                .collect(Collectors.toList());
        
        return PerfilListResponse.builder()
                .data(perfilesDTO)
                .total(perfilesPage.getTotalElements())
                .page(pageNumber + 1)
                .limit(pageSize)
                .build();
    }

    /**
     * Obtiene detalle de un perfil específico
     * @param id ID del perfil
     * @return Detalle del perfil con módulos asignados
     */
    @Transactional(readOnly = true)
    public PerfilDetalleDTO getPerfilById(Integer id) {
        log.debug("Obteniendo detalle de perfil ID: {}", id);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
        
        // Obtener módulos asignados
        List<Modulo> modulos = moduloRepository.findByPerfilId(id);
        List<ModuloDTO> modulosDTO = modulos.stream()
                .map(this::toModuloDTO)
                .collect(Collectors.toList());
        
        // Contar usuarios asignados
        Long usuariosAsignados = perfilRepository.countUsuariosAsignados(id);
        
        return PerfilDetalleDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getPrfNombre())
                .descripcion(perfil.getPrfDescrip())
                .estatus(perfil.getPrfSts())
                .nivel(perfil.getPrfNivel())
                .modulos(modulosDTO)
                .usuariosAsignados(usuariosAsignados)
                .fechaCreacion(perfil.getPrfFchHorCrea())
                .build();
    }

    /**
     * Crea un nuevo perfil
     * @param request Datos del perfil a crear
     * @param username Usuario que crea
     * @param estacion Estación desde donde se crea
     * @return Perfil creado
     */
    @Transactional
    public PerfilDTO createPerfil(CreatePerfilRequest request, String username, String estacion) {
        log.info("Creando perfil: {} por usuario: {}", request.getNombre(), username);
        
        // Validar que no exista un perfil con el mismo nombre
        if (perfilRepository.existsByPrfNombre(request.getNombre())) {
            throw new BadRequestException("Ya existe un perfil con el nombre: " + request.getNombre());
        }
        
        // Obtener usuario creador
        Persona usuario = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        
        // Crear perfil
        Perfil perfil = new Perfil();
        perfil.setPrfNombre(request.getNombre());
        perfil.setPrfDescrip(request.getDescripcion());
        perfil.setPrfNivel(request.getNivel());
        perfil.setPrfSts("A");
        perfil.setPrfUsrcrea(usuario);
        perfil.setPrfFchHorCrea(OffsetDateTime.now());
        perfil.setPrfEstCrea(estacion);
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);
        
        perfil = perfilRepository.save(perfil);
        
        log.info("Perfil creado exitosamente con ID: {}", perfil.getId());
        return toPerfilDTO(perfil);
    }

    /**
     * Actualiza un perfil existente
     * @param id ID del perfil a actualizar
     * @param request Datos a actualizar
     * @param username Usuario que modifica
     * @param estacion Estación desde donde se modifica
     * @return Perfil actualizado
     */
    @Transactional
    public PerfilDTO updatePerfil(Integer id, UpdatePerfilRequest request, String username, String estacion) {
        log.info("Actualizando perfil ID: {} por usuario: {}", id, username);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
        
        // Obtener usuario modificador
        Persona usuario = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        
        // Actualizar campos si están presentes
        boolean updated = false;
        
        if (request.getNombre() != null && !request.getNombre().equals(perfil.getPrfNombre())) {
            // Validar que no exista otro perfil con ese nombre
            if (perfilRepository.existsByPrfNombreAndIdNot(request.getNombre(), id)) {
                throw new BadRequestException("Ya existe otro perfil con el nombre: " + request.getNombre());
            }
            perfil.setPrfNombre(request.getNombre());
            updated = true;
        }
        
        if (request.getDescripcion() != null) {
            perfil.setPrfDescrip(request.getDescripcion());
            updated = true;
        }
        
        if (request.getEstatus() != null && !request.getEstatus().equals(perfil.getPrfSts())) {
            perfil.setPrfSts(request.getEstatus());
            updated = true;
        }
        
        if (request.getNivel() != null && !request.getNivel().equals(perfil.getPrfNivel())) {
            perfil.setPrfNivel(request.getNivel());
            updated = true;
        }
        
        if (updated) {
            perfil.setPrfUsrmod(usuario);
            perfil.setPrfFchHorMod(OffsetDateTime.now());
            perfil.setPrfEstMod(estacion);
            perfil = perfilRepository.save(perfil);
            log.info("Perfil ID: {} actualizado exitosamente", id);
        } else {
            log.debug("No hubo cambios en el perfil ID: {}", id);
        }
        
        return toPerfilDTO(perfil);
    }

    /**
     * Elimina (inactiva) un perfil
     * @param id ID del perfil a eliminar
     * @param username Usuario que elimina
     * @param estacion Estación desde donde se elimina
     */
    @Transactional
    public Map<String, String> deletePerfil(Integer id, String username, String estacion) {
        log.info("Eliminando (inactivando) perfil ID: {} por usuario: {}", id, username);
        
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
        
        // Obtener usuario modificador
        Persona usuario = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        
        // Inactivar perfil
        perfil.setPrfSts("I");
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);
        
        perfilRepository.save(perfil);
        
        log.info("Perfil ID: {} inactivado exitosamente", id);
        return Map.of("message", "Perfil inactivado correctamente");
    }

    /**
     * Asigna módulos a un perfil
     * @param id ID del perfil
     * @param request Lista de IDs de módulos a asignar
     * @param username Usuario que asigna
     * @param estacion Estación desde donde se asigna
     * @return Resultado de la operación
     */
    @Transactional
    public Map<String, Object> asignarModulos(Integer id, AsignarModulosRequest request, String username, String estacion) {
        log.info("Asignando {} módulos al perfil ID: {} por usuario: {}", 
                 request.getModulos().size(), id, username);
        
        // Validar que el perfil existe
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
        
        // Obtener usuario modificador
        Persona usuario = personaRepository.findByPerUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        
        // Validar que todos los módulos existen y están activos
        List<Modulo> modulos = moduloRepository.findActiveByIds(request.getModulos());
        if (modulos.size() != request.getModulos().size()) {
            throw new BadRequestException("Uno o más módulos no existen o están inactivos");
        }
        
        // Asignar módulos (actualizar si ya existe, crear si no)
        int asignados = 0;
        for (Modulo modulo : modulos) {
            ModPerfil existing = modPerfilRepository.findByPerfilIdAndModuloId(id, modulo.getModId());
            
            if (existing != null) {
                // Si existe pero está inactivo, reactivar
                if ("I".equals(existing.getMPSts())) {
                    existing.setMPSts("A");
                    existing.setMpUsrmod(usuario);
                    existing.setMPFchHorMod(OffsetDateTime.now());
                    existing.setMPEstMod(estacion);
                    modPerfilRepository.save(existing);
                    asignados++;
                }
            } else {
                // Crear nueva asignación
                ModPerfil modPerfil = new ModPerfil();
                
                ModPerfilId modPerfilId = new ModPerfilId();
                modPerfilId.setMpPrfid(id);
                modPerfilId.setMpModid(modulo.getModId());
                modPerfil.setId(modPerfilId);
                
                modPerfil.setMpPrfid(perfil);
                modPerfil.setMpModid(modulo);
                modPerfil.setMpid(modulo.getModId()); // Usar el ID del módulo como mpid
                modPerfil.setMPSts("A");
                modPerfil.setMpUsrcrea(usuario);
                modPerfil.setMPFchHorCrea(OffsetDateTime.now());
                modPerfil.setMPEstCrea(estacion);
                modPerfil.setMpUsrmod(usuario);
                modPerfil.setMPFchHorMod(OffsetDateTime.now());
                modPerfil.setMPEstMod(estacion);
                
                modPerfilRepository.save(modPerfil);
                asignados++;
            }
        }
        
        log.info("Se asignaron {} módulos al perfil ID: {}", asignados, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Módulos asignados correctamente");
        result.put("modulos_asignados", asignados);
        return result;
    }

    /**
     * Revoca un módulo de un perfil
     * @param perfilId ID del perfil
     * @param moduloId ID del módulo a revocar
     * @param username Usuario que revoca
     * @param estacion Estación desde donde se revoca
     * @return Resultado de la operación
     */
    @Transactional
    public Map<String, String> revocarModulo(Integer perfilId, Integer moduloId, String username, String estacion) {
        log.info("Revocando módulo ID: {} del perfil ID: {} por usuario: {}", moduloId, perfilId, username);
        
        // Validar que el perfil existe
        if (!perfilRepository.existsById(perfilId)) {
            throw new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId);
        }
        
        // Validar que el módulo existe
        if (!moduloRepository.existsById(moduloId)) {
            throw new ResourceNotFoundException("Módulo no encontrado con ID: " + moduloId);
        }
        
        // Inactivar asignación
        modPerfilRepository.deleteByPerfilIdAndModuloId(perfilId, moduloId);
        
        log.info("Módulo ID: {} revocado del perfil ID: {}", moduloId, perfilId);
        return Map.of("message", "Módulo removido del perfil");
    }

    // Métodos auxiliares de conversión
    
    private PerfilDTO toPerfilDTO(Perfil perfil) {
        Long usuariosAsignados = perfilRepository.countUsuariosAsignados(perfil.getId());
        
        return PerfilDTO.builder()
                .id(perfil.getId())
                .nombre(perfil.getPrfNombre())
                .descripcion(perfil.getPrfDescrip())
                .estatus(perfil.getPrfSts())
                .nivel(perfil.getPrfNivel())
                .fechaCreacion(perfil.getPrfFchHorCrea())
                .usuariosAsignados(usuariosAsignados)
                .build();
    }
    
    private ModuloDTO toModuloDTO(Modulo modulo) {
        return ModuloDTO.builder()
                .id(modulo.getModId())
                .nombre(modulo.getModNombre())
                .descripcion(modulo.getModDescrip())
                .nivel(modulo.getModNivel())
                .build();
    }
}
