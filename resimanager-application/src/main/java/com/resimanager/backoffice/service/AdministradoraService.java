package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.PerfPersAdministradora;
import com.resimanager.backoffice.domain.model.PerfPersAdministradoraId;
import com.resimanager.backoffice.domain.model.Perfil;
import com.resimanager.backoffice.domain.model.PerfilSimple;
import com.resimanager.backoffice.domain.model.PersAdministradora;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuarioConPerfiles;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.AdministradoraUseCase;
import com.resimanager.backoffice.domain.port.out.AdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfPersAdministradoraRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PerfilRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersAdministradoraRepositoryPort;
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
public class AdministradoraService implements AdministradoraUseCase {

    private final AdministradoraRepositoryPort administradoraRepositoryPort;
    private final PersAdministradoraRepositoryPort persAdministradoraRepositoryPort;
    private final PerfPersAdministradoraRepositoryPort perfPersAdministradoraRepositoryPort;
    private final PerfilRepositoryPort perfilRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Administradora> obtenerAdministradoras(Estatus estatus, String busqueda,
                                                                    int pagina, int limite) {
        return administradoraRepositoryPort.buscarConFiltros(estatus, busqueda, pagina, limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Administradora> obtenerAdministradora(Integer id) {
        return administradoraRepositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Administradora crearAdministradora(String docIdent, String nombre, String telefono, String email,
                                              Integer personaContactoId, String ejecutor, String estacion) {
        Persona ejecutorEntidad = findEjecutor(ejecutor);

        Administradora adm = new Administradora();
        adm.setId(administradoraRepositoryPort.siguienteId());
        adm.setAdmDocIdent(docIdent);
        adm.setAdmNombre(nombre);
        adm.setAdmTelefono(telefono);
        adm.setAdmEMail(email);
        adm.setAdmSts("A");
        adm.setAdmPersContacto(ejecutorEntidad);
        adm.setAdmUsrCrea(ejecutorEntidad);
        adm.setAdmFchHorCrea(OffsetDateTime.now());
        adm.setAdmEstCrea(estacion);
        adm.setAdmUsrMod(ejecutorEntidad);
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        return administradoraRepositoryPort.guardar(adm);
    }

    @Override
    @Transactional
    public Administradora actualizarAdministradora(Integer id, String docIdent, String nombre, String telefono,
                                                   String email, Estatus estatus, String ejecutor, String estacion) {
        Administradora adm = administradoraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));

        if (docIdent != null) adm.setAdmDocIdent(docIdent);
        if (nombre != null) adm.setAdmNombre(nombre);
        if (telefono != null) adm.setAdmTelefono(telefono);
        if (email != null) adm.setAdmEMail(email);
        if (estatus != null) adm.setAdmSts(estatus.codigo());

        adm.setAdmUsrMod(findEjecutor(ejecutor));
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        return administradoraRepositoryPort.guardar(adm);
    }

    @Override
    @Transactional
    public void inactivarAdministradora(Integer id, String ejecutor, String estacion) {
        Administradora adm = administradoraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + id));

        adm.setAdmSts("I");
        adm.setAdmUsrMod(findEjecutor(ejecutor));
        adm.setAdmFchHorMod(OffsetDateTime.now());
        adm.setAdmEstMod(estacion);

        administradoraRepositoryPort.guardar(adm);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuariosContexto obtenerUsuariosAdministradora(Integer admId) {
        Administradora administradora = administradoraRepositoryPort.buscarPorId(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        List<PersAdministradora> persAdministradoras =
                persAdministradoraRepositoryPort.listarActivasPorAdministradoraId(admId);

        List<UsuarioConPerfiles> usuarios = persAdministradoras.stream().map(pa -> {
            Persona persona = pa.getPaPerid();
            org.hibernate.Hibernate.initialize(persona);
            List<PerfPersAdministradora> perfiles =
                    perfPersAdministradoraRepositoryPort.listarActivasPorAdministradoraIdYPersonaId(admId, persona.getId());
            List<PerfilSimple> perfilesSimple = perfiles.stream()
                    .map(ppa -> new PerfilSimple(ppa.getPpaPrfid().getId(), ppa.getPpaPrfid().getPrfNombre()))
                    .toList();
            return new UsuarioConPerfiles(persona, perfilesSimple);
        }).toList();

        return new UsuariosContexto(administradora.getId(), administradora.getAdmNombre(), usuarios);
    }

    @Override
    @Transactional
    public ResultadoAsignacion asignarPerfilesAUsuario(Integer admId, Integer usuarioId, List<Integer> perfilIds,
                                                       String ejecutor, String estacion) {
        administradoraRepositoryPort.buscarPorId(admId)
                .orElseThrow(() -> new ResourceNotFoundException("Administradora no encontrada con ID: " + admId));

        personaRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + ejecutor));

        int asignados = 0;
        int reactivados = 0;

        for (Integer perfilId : perfilIds) {
            Perfil perfil = perfilRepositoryPort.buscarPorId(perfilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + perfilId));

            Optional<PerfPersAdministradora> existing =
                    perfPersAdministradoraRepositoryPort.buscarPorId(admId, usuarioId, perfilId);

            if (existing.isPresent()) {
                PerfPersAdministradora ppa = existing.get();
                if ("I".equals(ppa.getPPASts())) {
                    ppa.setPPASts("A");
                    ppa.setPpaUsrmod(ejecutorEntidad);
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
                ppa.setPpaUsrcrea(ejecutorEntidad);
                ppa.setPPAFchHorCrea(OffsetDateTime.now());
                ppa.setPPAEstCrea(estacion);
                ppa.setPpaUsrmod(ejecutorEntidad);
                ppa.setPPAFchHorMod(OffsetDateTime.now());
                ppa.setPPAEstMod(estacion);

                perfPersAdministradoraRepositoryPort.guardar(ppa);
                asignados++;
            }
        }

        return new ResultadoAsignacion(asignados, reactivados);
    }

    @Override
    @Transactional
    public void removerPerfilDeUsuario(Integer admId, Integer usuarioId, Integer perfilId,
                                       String ejecutor, String estacion) {
        personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + ejecutor));

        PerfPersAdministradora ppa = perfPersAdministradoraRepositoryPort.buscarPorId(admId, usuarioId, perfilId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de perfil no encontrada para administradora ID: " + admId
                        + ", usuario ID: " + usuarioId + ", perfil ID: " + perfilId));

        Persona ejecutorEntidad = personaRepositoryPort.buscarPorUsuario(ejecutor).orElseThrow();
        ppa.setPPASts("I");
        ppa.setPpaUsrmod(ejecutorEntidad);
        ppa.setPPAFchHorMod(OffsetDateTime.now());
        ppa.setPPAEstMod(estacion);

        perfPersAdministradoraRepositoryPort.guardar(ppa);
    }

    private Persona findEjecutor(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario ejecutor no encontrado: " + username));
    }
}