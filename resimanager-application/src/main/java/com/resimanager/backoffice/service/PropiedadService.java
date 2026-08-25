package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.in.PropiedadUseCase;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropiedadRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropiedadService implements PropiedadUseCase {

    private final PropiedadRepositoryPort propiedadRepositoryPort;
    private final ConjuntoRepositoryPort conjuntoRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Propiedad> obtenerPropiedades(Estatus estatus, Integer conjuntoId,
                                                           String busqueda, int pagina, int limite) {
        return propiedadRepositoryPort.buscarConFiltros(estatus, conjuntoId, busqueda, pagina, limite);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Propiedad> obtenerPropiedad(Integer id) {
        return propiedadRepositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Propiedad crearPropiedad(Integer conjId, Integer cdpId, String numero,
                                    BigDecimal cantidad, BigDecimal coefParticipacion,
                                    String ejecutor, String estacion) {
        Propiedad prop = new Propiedad();
        prop.setPpConjId(conjId);
        prop.setPpCdpId(cdpId);
        prop.setPpNumero(numero);
        prop.setPpCantidad(cantidad);
        prop.setPpCoefParticipacion(coefParticipacion);
        prop.setPpSts("A");
        prop.setPpUsrCrea(findPersonaIdByUsuario(ejecutor));
        prop.setPpFchHorCrea(OffsetDateTime.now());
        prop.setPpEstCrea(estacion);
        prop.setPpUsrMod(findPersonaIdByUsuario(ejecutor));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return propiedadRepositoryPort.guardar(prop);
    }

    @Override
    @Transactional
    public Propiedad actualizarPropiedad(Integer id, Integer cdpId, String numero,
                                         BigDecimal cantidad, BigDecimal coefParticipacion,
                                         Estatus estatus, String ejecutor, String estacion) {
        Propiedad prop = propiedadRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        if (cdpId != null) prop.setPpCdpId(cdpId);
        if (numero != null) prop.setPpNumero(numero);
        if (cantidad != null) prop.setPpCantidad(cantidad);
        if (coefParticipacion != null) prop.setPpCoefParticipacion(coefParticipacion);
        if (estatus != null) prop.setPpSts(estatus.codigo());

        prop.setPpUsrMod(findPersonaIdByUsuario(ejecutor));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return propiedadRepositoryPort.guardar(prop);
    }

    @Override
    @Transactional
    public void inactivarPropiedad(Integer id, String ejecutor, String estacion) {
        Propiedad prop = propiedadRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        prop.setPpSts("I");
        prop.setPpUsrMod(findPersonaIdByUsuario(ejecutor));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        propiedadRepositoryPort.guardar(prop);
    }

    private Integer findPersonaIdByUsuario(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .map(com.resimanager.backoffice.domain.model.Persona::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
    }
}