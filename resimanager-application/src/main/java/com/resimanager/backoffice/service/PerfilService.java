package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.ModPerfil;
import com.resimanager.backoffice.domain.model.ModPerfilId;
import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PerfilDetalle;
import com.resimanager.backoffice.domain.model.PerfilResumen;
import com.resimanager.backoffice.domain.model.PermisoModulo;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.PerfilUseCase;
import com.resimanager.backoffice.domain.port.out.AccOpcPerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ModPerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ModuloRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.exception.BadRequestException;
import com.resimanager.backoffice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilService implements PerfilUseCase {

    private final PerfilRepositoryPort perfilRepositoryPort;
    private final ModuloRepositoryPort moduloRepositoryPort;
    private final ModPerfilRepositoryPort modPerfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final AccOpcPerfilRepositoryPort accOpcPerfilRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<PerfilResumen> obtenerPerfiles(Estatus estatus, Integer nivel, String busqueda,
                                                            int pagina, int limite) {
        ResultadoPaginado<Perfil> page = perfilRepositoryPort.buscarConFiltros(estatus, nivel, busqueda,
                pagina > 0 ? pagina : 1, limite > 0 ? limite : 25);

        List<PerfilResumen> resumenes = page.datos().stream()
                .map(perfil -> new PerfilResumen(perfil, perfilRepositoryPort.contarUsuariosAsignados(perfil.getId())))
                .toList();

        return new ResultadoPaginado<>(resumenes, page.total(), page.pagina(), page.limite());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Perfil> obtenerPerfil(Integer id) {
        return perfilRepositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilDetalle obtenerDetallePerfil(Integer id) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        List<Modulo> modulos = moduloRepositoryPort.listarPorPerfilId(id);
        List<PermisoModulo> permisos = accOpcPerfilRepositoryPort.listarPermisosPorPerfilId(id);
        long usuariosAsignados = perfilRepositoryPort.contarUsuariosAsignados(id);

        return new PerfilDetalle(perfil, modulos, permisos, usuariosAsignados);
    }

    @Override
    @Transactional
    public Perfil crearPerfil(String nombre, String descripcion, Integer nivel,
                              String ejecutor, String estacion) {
        if (perfilRepositoryPort.existePorNombre(nombre)) {
            throw new BadRequestException("Ya existe un perfil con el nombre: " + nombre);
        }

        Persona usuario = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + ejecutor));

        Perfil perfil = new Perfil();
        perfil.setPrfNombre(nombre);
        perfil.setPrfDescrip(descripcion);
        perfil.setPrfNivel(nivel);
        perfil.setPrfSts("A");
        perfil.setPrfUsrcrea(usuario);
        perfil.setPrfFchHorCrea(OffsetDateTime.now());
        perfil.setPrfEstCrea(estacion);
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);

        return perfilRepositoryPort.guardar(perfil);
    }

    @Override
    @Transactional
    public Perfil actualizarPerfil(Integer id, String nombre, String descripcion, Integer nivel,
                                   Estatus estatus, String ejecutor, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + ejecutor));

        boolean updated = false;

        if (nombre != null && !nombre.equals(perfil.getPrfNombre())) {
            if (perfilRepositoryPort.existePorNombreYDistintoId(nombre, id)) {
                throw new BadRequestException("Ya existe otro perfil con el nombre: " + nombre);
            }
            perfil.setPrfNombre(nombre);
            updated = true;
        }

        if (descripcion != null) {
            perfil.setPrfDescrip(descripcion);
            updated = true;
        }

        if (estatus != null && !estatus.codigo().equals(perfil.getPrfSts())) {
            perfil.setPrfSts(estatus.codigo());
            updated = true;
        }

        if (nivel != null && !nivel.equals(perfil.getPrfNivel())) {
            perfil.setPrfNivel(nivel);
            updated = true;
        }

        if (updated) {
            perfil.setPrfUsrmod(usuario);
            perfil.setPrfFchHorMod(OffsetDateTime.now());
            perfil.setPrfEstMod(estacion);
            perfil = perfilRepositoryPort.guardar(perfil);
        }

        return perfil;
    }

    @Override
    @Transactional
    public void inactivarPerfil(Integer id, String ejecutor, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + ejecutor));

        perfil.setPrfSts("I");
        perfil.setPrfUsrmod(usuario);
        perfil.setPrfFchHorMod(OffsetDateTime.now());
        perfil.setPrfEstMod(estacion);

        perfilRepositoryPort.guardar(perfil);
    }

    @Override
    @Transactional
    public int asignarModulos(Integer perfilId, List<Integer> moduloIds, String ejecutor, String estacion) {
        Perfil perfil = perfilRepositoryPort.buscarPorId(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

        Persona usuario = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + ejecutor));

        List<Modulo> modulos = moduloRepositoryPort.buscarActivosPorIds(moduloIds);
        if (modulos.size() != moduloIds.size()) {
            throw new BadRequestException("Uno o más módulos no existen o están inactivos");
        }

        int asignados = 0;
        for (Modulo modulo : modulos) {
            ModPerfil existing = modPerfilRepositoryPort.buscarPorId(perfilId, modulo.getModId()).orElse(null);

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
                modPerfilId.setMpPrfid(perfilId);
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

        return asignados;
    }

    @Override
    @Transactional
    public void revocarModulo(Integer perfilId, Integer moduloId, String ejecutor, String estacion) {
        if (perfilRepositoryPort.buscarPorId(perfilId).isEmpty()) {
            throw new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId);
        }

        if (!moduloRepositoryPort.existePorId(moduloId)) {
            throw new ResourceNotFoundException("Módulo no encontrado con ID: " + moduloId);
        }

        modPerfilRepositoryPort.eliminar(perfilId, moduloId);
    }
}