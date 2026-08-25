package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.ConjuntoRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropiedadRepositoryPort;
import com.resimanager.backoffice.dto.PropiedadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropiedadService {

    private final PropiedadRepositoryPort propiedadRepositoryPort;
    private final ConjuntoRepositoryPort conjuntoRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;

    public PropiedadDTO.ListResponse getPropiedades(String estatus, Integer conjuntoId, String search, Integer page, Integer limit) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Propiedad> result =
                propiedadRepositoryPort.buscarConFiltros(estatusParam, conjuntoId, search, page, limit);

        return new PropiedadDTO.ListResponse(
                result.datos().stream().map(this::toDTO).toList(),
                result.total(),
                page,
                limit
        );
    }

    public PropiedadDTO getPropiedadById(Integer id) {
        Propiedad prop = propiedadRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));
        return toDTO(prop);
    }

    @Transactional
    public PropiedadDTO createPropiedad(Integer conjId, Integer cdpId, String numero,
                                         BigDecimal cantidad, BigDecimal coefParticipacion,
                                         String username, String estacion) {
        Propiedad prop = new Propiedad();
        prop.setPpConjId(conjId);
        prop.setPpCdpId(cdpId);
        prop.setPpNumero(numero);
        prop.setPpCantidad(cantidad);
        prop.setPpCoefParticipacion(coefParticipacion);
        prop.setPpSts("A");
        prop.setPpUsrCrea(findPersonaIdByUsuario(username));
        prop.setPpFchHorCrea(OffsetDateTime.now());
        prop.setPpEstCrea(estacion);
        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return toDTO(propiedadRepositoryPort.guardar(prop));
    }

    @Transactional
    public PropiedadDTO updatePropiedad(Integer id, Integer cdpId, String numero,
                                         BigDecimal cantidad, BigDecimal coefParticipacion,
                                         String estatus, String username, String estacion) {
        Propiedad prop = propiedadRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        if (cdpId != null) prop.setPpCdpId(cdpId);
        if (numero != null) prop.setPpNumero(numero);
        if (cantidad != null) prop.setPpCantidad(cantidad);
        if (coefParticipacion != null) prop.setPpCoefParticipacion(coefParticipacion);
        if (estatus != null && (estatus.equals("A") || estatus.equals("I"))) prop.setPpSts(estatus);

        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return toDTO(propiedadRepositoryPort.guardar(prop));
    }

    @Transactional
    public Map<String, String> deletePropiedad(Integer id, String username, String estacion) {
        Propiedad prop = propiedadRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        prop.setPpSts("I");
        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        propiedadRepositoryPort.guardar(prop);
        return Map.of("message", "Propiedad inactivada correctamente");
    }

    private PropiedadDTO toDTO(Propiedad p) {
        String conjNombre = null;
        Optional<Conjunto> conj = conjuntoRepositoryPort.buscarPorId(p.getPpConjId());
        if (conj.isPresent()) {
            conjNombre = conj.get().getConjNombre();
        }

        return PropiedadDTO.builder()
                .ppid(p.getPpid())
                .ppConjId(p.getPpConjId())
                .conjuntoNombre(conjNombre)
                .ppCdpId(p.getPpCdpId())
                .ppNumero(p.getPpNumero())
                .ppCantidad(p.getPpCantidad())
                .ppCoefParticipacion(p.getPpCoefParticipacion())
                .estatus(p.getPpSts())
                .build();
    }

    private Integer findPersonaIdByUsuario(String username) {
        return personaRepositoryPort.buscarPorUsuario(username)
                .map(com.resimanager.backoffice.domain.model.Persona::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
    }
}