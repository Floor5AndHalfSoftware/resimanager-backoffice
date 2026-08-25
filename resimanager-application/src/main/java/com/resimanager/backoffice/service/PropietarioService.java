package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.PropietarioId;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.PropietarioUseCase;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropiedadRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropietarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropietarioService implements PropietarioUseCase {

    private final PropietarioRepositoryPort propietarioRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final PropiedadRepositoryPort propiedadRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Propietario> obtenerPropietarios(Estatus estatus, Integer conjuntoId,
                                                              String busqueda, int pagina, int limite) {
        return propietarioRepositoryPort.buscarConFiltros(estatus, conjuntoId, busqueda, pagina, limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Propietario> obtenerPropietario(Integer conjId, Integer perId) {
        return propietarioRepositoryPort.buscarPorId(conjId, perId);
    }

    @Override
    @Transactional
    public Propietario crearPropietario(Integer conjId, Integer perId, Integer propiedadId,
                                        LocalDate fchDesde, LocalDate fchHasta, String ejecutor, String estacion) {
        if (propietarioRepositoryPort.existePorId(conjId, perId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El propietario ya existe en este conjunto");
        }

        personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        Propietario propietario = new Propietario();
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);
        propietario.setId(id);
        propietario.setPptID(propiedadId);
        propietario.setPptFchDesde(fchDesde);
        propietario.setPptSts("A");
        propietario.setPptFchHorCrea(OffsetDateTime.now());
        propietario.setPptEstCrea(estacion);
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return propietarioRepositoryPort.guardar(propietario);
    }

    @Override
    @Transactional
    public Propietario actualizarPropietario(Integer conjId, Integer perId, Integer propiedadId,
                                             LocalDate fchDesde, LocalDate fchHasta, Estatus estatus,
                                             String ejecutor, String estacion) {
        Propietario propietario = propietarioRepositoryPort.buscarPorId(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        personaRepositoryPort.buscarPorUsuario(ejecutor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        if (propiedadId != null) propietario.setPptID(propiedadId);
        if (fchDesde != null) propietario.setPptFchDesde(fchDesde);
        if (fchHasta != null) propietario.setPptFchHasta(fchHasta);
        if (estatus != null) propietario.setPptSts(estatus.codigo());

        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return propietarioRepositoryPort.guardar(propietario);
    }

    @Override
    @Transactional
    public void inactivarPropietario(Integer conjId, Integer perId, String ejecutor, String estacion) {
        Propietario propietario = propietarioRepositoryPort.buscarPorId(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        propietario.setPptSts("I");
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        propietarioRepositoryPort.guardar(propietario);
    }
}