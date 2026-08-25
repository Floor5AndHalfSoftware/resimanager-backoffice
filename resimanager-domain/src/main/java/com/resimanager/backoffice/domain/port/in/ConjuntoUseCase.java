package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Casos de uso de gestión de conjuntos.
 */
public interface ConjuntoUseCase {

    ResultadoPaginado<Conjunto> obtenerConjuntos(Estatus estatus, String busqueda, int pagina, int limite);

    Optional<Conjunto> obtenerConjunto(Integer id);

    Conjunto crearConjunto(String docIdent, String nombre, String telefono, String email,
                           Integer personaContactoId, String ejecutor, String estacion);

    Conjunto actualizarConjunto(Integer id, String docIdent, String nombre, String telefono, String email,
                                Integer personaContactoId, Estatus estatus, String ejecutor, String estacion);

    void inactivarConjunto(Integer id, String ejecutor, String estacion);

    UsuariosContexto obtenerUsuariosConjunto(Integer conjuntoId);

    ResultadoAsignacion asignarPerfilesAUsuario(Integer conjuntoId, Integer usuarioId,
                                                List<Integer> perfilIds, String ejecutor, String estacion);

    void removerPerfilDeUsuario(Integer conjuntoId, Integer usuarioId, Integer perfilId,
                                String ejecutor, String estacion);
}