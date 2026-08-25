package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.PerfilesUsuario;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService implements UsuarioUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PerfPersAdministradoraRepositoryPort perfPersAdministradoraRepositoryPort;
    private final PerfPersConjuntoRepositoryPort perfPersConjuntoRepositoryPort;
    private final AdministradoraRepositoryPort administradoraRepositoryPort;
    private final ConjuntoRepositoryPort conjuntoRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Persona> obtenerUsuarios(Estatus estatus, String busqueda, int pagina, int limite) {
        return personaRepositoryPort.buscarConFiltros(estatus, busqueda, pagina, limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Persona> obtenerUsuario(Integer usuarioId) {
        return personaRepositoryPort.buscarPorId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilesUsuario obtenerPerfilesDeUsuario(Integer personaId) {
        Persona persona = personaRepositoryPort.buscarPorId(personaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<PerfilesUsuario.ContextoPerfil> contextos = new ArrayList<>();

        var ppaList = perfPersAdministradoraRepositoryPort.listarActivasPorPersonaId(personaId);
        var ppaByAdm = ppaList.stream()
                .collect(Collectors.groupingBy(ppa -> ppa.getId().getPpaAdmid()));

        if (!ppaByAdm.isEmpty()) {
            List<Administradora> admins = administradoraRepositoryPort.buscarActivasPorIds(List.copyOf(ppaByAdm.keySet()));
            for (Administradora adm : admins) {
                List<PerfilesUsuario.PerfilSimple> perfiles = ppaByAdm.get(adm.getId()).stream()
                        .map(ppa -> new PerfilesUsuario.PerfilSimple(
                                ppa.getPpaPrfid().getId(), ppa.getPpaPrfid().getPrfNombre()))
                        .toList();
                contextos.add(new PerfilesUsuario.ContextoPerfil(
                        "ADMINISTRADORA", adm.getId(), adm.getAdmNombre(), perfiles));
            }
        }

        var ppcList = perfPersConjuntoRepositoryPort.listarActivasPorPersonaId(personaId);
        var ppcByConj = ppcList.stream()
                .collect(Collectors.groupingBy(ppc -> ppc.getId().getPpcConjid()));

        if (!ppcByConj.isEmpty()) {
            List<Conjunto> conjuntos = conjuntoRepositoryPort.buscarActivosPorIds(List.copyOf(ppcByConj.keySet()));
            for (Conjunto conj : conjuntos) {
                List<PerfilesUsuario.PerfilSimple> perfiles = ppcByConj.get(conj.getId()).stream()
                        .map(ppc -> new PerfilesUsuario.PerfilSimple(
                                ppc.getPpcPrfid().getId(), ppc.getPpcPrfid().getPrfNombre()))
                        .toList();
                contextos.add(new PerfilesUsuario.ContextoPerfil(
                        "CONJUNTO", conj.getId(), conj.getConjNombre(), perfiles));
            }
        }

        return new PerfilesUsuario(persona, contextos);
    }

    @Override
    @Transactional
    public Persona actualizarUsuario(Integer id, String nombre, String apellido, String telefono,
                                     String email, Estatus estatus, String ejecutor, String estacion) {
        Persona persona = personaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        if (nombre != null && !nombre.isBlank()) persona.setPerNombre(nombre.trim());
        if (apellido != null && !apellido.isBlank()) persona.setPerApellido(apellido.trim());
        if (telefono != null && !telefono.isBlank()) persona.setPerTlfCel(telefono.trim());
        if (email != null && !email.isBlank()) persona.setPerEMail(email.trim());
        if (estatus != null) persona.setPerSts(estatus.codigo());

        persona.setPerUsrmod(ejecutorEntidad);
        persona.setPerFchHorMod(OffsetDateTime.now());
        persona.setPerEstMod(estacion);

        return personaRepositoryPort.guardar(persona);
    }

    @Override
    @Transactional
    public void inactivarUsuario(Integer id, String ejecutor, String estacion) {
        Persona persona = personaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        persona.setPerSts("I");
        persona.setPerUsrmod(ejecutorEntidad);
        persona.setPerFchHorMod(OffsetDateTime.now());
        persona.setPerEstMod(estacion);

        personaRepositoryPort.guardar(persona);
    }
}