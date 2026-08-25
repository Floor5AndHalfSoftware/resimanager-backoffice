package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjunto;
import com.resimanager.backoffice.domain.model.PerfPersConjuntoId;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PerfilSimple;
import com.resimanager.backoffice.domain.model.PersConjunto;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuarioConPerfiles;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.ConjuntoUseCase;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
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
public class ConjuntoService implements ConjuntoUseCase {

    private final ConjuntoRepositoryPort conjuntoRepositoryPort;
    private final PersConjuntoRepositoryPort persConjuntoRepositoryPort;
    private final PerfPersConjuntoRepositoryPort perfPersConjuntoRepositoryPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Conjunto> obtenerConjuntos(Estatus estatus, String busqueda, int pagina, int limite) {
        return conjuntoRepositoryPort.buscarConFiltros(estatus, busqueda, pagina, limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conjunto> obtenerConjunto(Integer id) {
        return conjuntoRepositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Conjunto crearConjunto(String docIdent, String nombre, String telefono, String email,
                                  Integer personaContactoId, String ejecutor, String estacion) {
        Persona contacto = personaRepositoryPort.buscarPorId(personaContactoId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona contacto no encontrada con ID: " + personaContactoId));

        Persona ejecutorEntidad = findEjecutor(ejecutor);

        Conjunto conj = new Conjunto();
        conj.setConjDocIdent(docIdent);
        conj.setConjNombre(nombre);
        conj.setConjTelefono(telefono);
        conj.setConjEMail(email);
        conj.setConjPersContacto(contacto);
        conj.setConjOrigen("A");
        conj.setConjSts("A");
        conj.setConjUsrCrea(ejecutorEntidad);
        conj.setConjFchHorCrea(OffsetDateTime.now());
        conj.setConjEstCrea(estacion);
        conj.setConjUsrMod(ejecutorEntidad);
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        return conjuntoRepositoryPort.guardar(conj);
    }

    @Override
    @Transactional
    public Conjunto actualizarConjunto(Integer id, String docIdent, String nombre, String telefono, String email,
                                       Integer personaContactoId, Estatus estatus, String ejecutor, String estacion) {
        Conjunto conj = conjuntoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        if (docIdent != null) conj.setConjDocIdent(docIdent);
        if (nombre != null) conj.setConjNombre(nombre);
        if (telefono != null) conj.setConjTelefono(telefono);
        if (email != null) conj.setConjEMail(email);
        if (personaContactoId != null) {
            Persona contacto = personaRepositoryPort.buscarPorId(personaContactoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona contacto no encontrada con ID: " + personaContactoId));
            conj.setConjPersContacto(contacto);
        }
        if (estatus != null) conj.setConjSts(estatus.codigo());

        conj.setConjUsrMod(findEjecutor(ejecutor));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        return conjuntoRepositoryPort.guardar(conj);
    }

    @Override
    @Transactional
    public void inactivarConjunto(Integer id, String ejecutor, String estacion) {
        Conjunto conj = conjuntoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + id));

        conj.setConjSts("I");
        conj.setConjUsrMod(findEjecutor(ejecutor));
        conj.setConjFchHorMod(OffsetDateTime.now());
        conj.setConjEstMod(estacion);

        conjuntoRepositoryPort.guardar(conj);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuariosContexto obtenerUsuariosConjunto(Integer conjId) {
        Conjunto conjunto = conjuntoRepositoryPort.buscarPorId(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        List<PersConjunto> persConjuntos = persConjuntoRepositoryPort.listarActivasPorConjuntoId(conjId);

        List<UsuarioConPerfiles> usuarios = persConjuntos.stream().map(pc -> {
            Persona persona = pc.getPcPerid();
            org.hibernate.Hibernate.initialize(persona);
            List<PerfPersConjunto> perfiles =
                    perfPersConjuntoRepositoryPort.listarActivasPorConjuntoIdYPersonaId(conjId, persona.getId());
            List<PerfilSimple> perfilesSimple = perfiles.stream()
                    .map(ppc -> new PerfilSimple(ppc.getPpcPrfid().getId(), ppc.getPpcPrfid().getPrfNombre()))
                    .toList();
            return new UsuarioConPerfiles(persona, perfilesSimple);
        }).toList();

        return new UsuariosContexto(conjunto.getId(), conjunto.getConjNombre(), usuarios);
    }

    @Override
    @Transactional
    public ResultadoAsignacion asignarPerfilesAUsuario(Integer conjId, Integer usuarioId, List<Integer> perfilIds,
                                                       String ejecutor, String estacion) {
        conjuntoRepositoryPort.buscarPorId(conjId)
                .orElseThrow(() -> new ResourceNotFoundException("Conjunto no encontrado con ID: " + conjId));

        personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + ejecutor));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : perfilIds) {
            Perfil perfil = perfilRepositoryPort.buscarPorId(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            Optional<PerfPersConjunto> existing =
                    perfPersConjuntoRepositoryPort.buscarPorId(conjId, usuarioId, perfilId);

            if (existing.isPresent()) {
                PerfPersConjunto ppc = existing.get();
                if ("I".equals(ppc.getPPCSts())) {
                    ppc.setPPCSts("A");
                    ppc.setPpcUsrmod(ejecutorEntidad);
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
                ppc.setPpcUsrcrea(ejecutorEntidad);
                ppc.setPPCFchHorCrea(OffsetDateTime.now());
                ppc.setPPCEstCrea(estacion);
                ppc.setPpcUsrmod(ejecutorEntidad);
                ppc.setPPCFchHorMod(OffsetDateTime.now());
                ppc.setPPCEstMod(estacion);

                perfPersConjuntoRepositoryPort.guardar(ppc);
                asignados++;
            }
        }

        return new ResultadoAsignacion(asignados, reactivados);
    }

    @Override
    @Transactional
    public void removerPerfilDeUsuario(Integer conjId, Integer usuarioId, Integer perfilId,
                                       String ejecutor, String estacion) {
        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + ejecutor));

        PerfPersConjunto ppc = perfPersConjuntoRepositoryPort.buscarPorId(conjId, usuarioId, perfilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para conjunto ID: " + conjId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        ppc.setPPCSts("I");
        ppc.setPpcUsrmod(ejecutorEntidad);
        ppc.setPPCFchHorMod(OffsetDateTime.now());
        ppc.setPPCEstMod(estacion);

        perfPersConjuntoRepositoryPort.guardar(ppc);
    }

    private Persona findEjecutor(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));
    }
}