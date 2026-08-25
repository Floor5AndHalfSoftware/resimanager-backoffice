package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.PropietarioId;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropiedadRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PropietarioRepositoryPort;
import com.resimanager.backoffice.dto.PropietarioDTO;
import com.resimanager.backoffice.dto.PropietarioListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropietarioService {

    private final PropietarioRepositoryPort propietarioRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final PropiedadRepositoryPort propiedadRepositoryPort;

    public PropietarioListResponse getPropietarios(String estatus, Integer conjuntoId, String search, Integer page, Integer limit) {
        Estatus estatusParam = (estatus != null && !estatus.isBlank()) ? Estatus.desdeCodigo(estatus.trim()) : null;

        ResultadoPaginado<Propietario> result =
                propietarioRepositoryPort.buscarConFiltros(estatusParam, conjuntoId, search, page, limit);

        return PropietarioListResponse.builder()
                .data(result.datos().stream().map(this::toDTO).toList())
                .total(result.total())
                .page(page)
                .limit(limit)
                .build();
    }

    public PropietarioDTO getPropietarioById(Integer conjId, Integer perId) {
        Propietario propietario = propietarioRepositoryPort.buscarPorId(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));
        return toDTO(propietario);
    }

    @Transactional
    public PropietarioDTO createPropietario(Integer conjId, Integer perId, Integer propiedadId,
                                            String fechaDesde, String executorUsername, String estacion) {
        if (propietarioRepositoryPort.existePorId(conjId, perId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El propietario ya existe en este conjunto");
        }

        personaRepositoryPort.buscarPorUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        Propietario propietario = new Propietario();
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);
        propietario.setId(id);
        propietario.setPptID(propiedadId);
        propietario.setPptFchDesde(java.time.LocalDate.parse(fechaDesde));
        propietario.setPptSts("A");
        propietario.setPptFchHorCrea(OffsetDateTime.now());
        propietario.setPptEstCrea(estacion);
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return toDTO(propietarioRepositoryPort.guardar(propietario));
    }

    @Transactional
    public PropietarioDTO updatePropietario(Integer conjId, Integer perId, Integer propiedadId,
                                            String fechaDesde, String fechaHasta, String estatus,
                                            String executorUsername, String estacion) {
        Propietario propietario = propietarioRepositoryPort.buscarPorId(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        personaRepositoryPort.buscarPorUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        if (propiedadId != null) propietario.setPptID(propiedadId);
        if (fechaDesde != null) propietario.setPptFchDesde(java.time.LocalDate.parse(fechaDesde));
        if (fechaHasta != null) propietario.setPptFchHasta(java.time.LocalDate.parse(fechaHasta));
        if (estatus != null && (estatus.equals("A") || estatus.equals("I"))) propietario.setPptSts(estatus);

        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return toDTO(propietarioRepositoryPort.guardar(propietario));
    }

    @Transactional
    public Map<String, String> deletePropietario(Integer conjId, Integer perId, String executorUsername, String estacion) {
        Propietario propietario = propietarioRepositoryPort.buscarPorId(conjId, perId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        propietario.setPptSts("I");
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        propietarioRepositoryPort.guardar(propietario);
        return Map.of("message", "Propietario inactivado correctamente");
    }

    private PropietarioDTO toDTO(Propietario p) {
        String nombrePersona = null;
        String apellidoPersona = null;
        String documentoPersona = null;
        String nombrePropiedad = null;

        Optional<Persona> persona = personaRepositoryPort.buscarPorId(p.getId().getPptPerid());
        if (persona.isPresent()) {
            nombrePersona = persona.get().getPerNombre();
            apellidoPersona = persona.get().getPerApellido();
            documentoPersona = persona.get().getPerDocIdent();
        }

        Optional<Propiedad> prop = propiedadRepositoryPort.buscarPorPpidYConjuntoId(p.getPptID(), p.getId().getPptConjid());
        if (prop.isPresent()) {
            nombrePropiedad = prop.get().getPpNumero();
        }

        return PropietarioDTO.builder()
                .conjId(p.getId().getPptConjid())
                .perId(p.getId().getPptPerid())
                .personaNombre(nombrePersona)
                .personaApellido(apellidoPersona)
                .personaDocumento(documentoPersona)
                .propiedadId(p.getPptID())
                .propiedadNombre(nombrePropiedad)
                .fechaDesde(p.getPptFchDesde() != null ? p.getPptFchDesde().toString() : null)
                .fechaHasta(p.getPptFchHasta() != null ? p.getPptFchHasta().toString() : null)
                .estatus(p.getPptSts())
                .build();
    }
}