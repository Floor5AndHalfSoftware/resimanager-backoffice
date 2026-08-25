package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Administradora;
import com.resimanager.backoffice.domain.model.ResultadoAsignacion;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.UsuariosContexto;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.List;
import java.util.Optional;

/**
 * Casos de uso de gestión de administradoras.
 */
public interface AdministradoraUseCase {

    ResultadoPaginado<Administradora> obtenerAdministradoras(Estatus estatus, String busqueda,
                                                             int pagina, int limite);

    Optional<Administradora> obtenerAdministradora(Integer id);

    Administradora crearAdministradora(String docIdent, String nombre, String telefono, String email,
                                       Integer personaContactoId, String ejecutor, String estacion);

    Administradora actualizarAdministradora(Integer id, String docIdent, String nombre, String telefono,
                                            String email, Estatus estatus, String ejecutor, String estacion);

    void inactivarAdministradora(Integer id, String ejecutor, String estacion);

    UsuariosContexto obtenerUsuariosAdministradora(Integer administradoraId);

    ResultadoAsignacion asignarPerfilesAUsuario(Integer administradoraId, Integer usuarioId,
                                                List<Integer> perfilIds, String ejecutor, String estacion);

    void removerPerfilDeUsuario(Integer administradoraId, Integer usuarioId, Integer perfilId,
                                String ejecutor, String estacion);
}