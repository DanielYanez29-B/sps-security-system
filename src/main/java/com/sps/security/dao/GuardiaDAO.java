
package com.sps.security.dao;

import com.sps.security.model.operative.Guardia;

import java.util.List;
import java.util.Optional;

public interface GuardiaDAO extends CrudDAO<Guardia, Integer> {

    List<Guardia> obtenerGuardiasActivos();

    Optional<Guardia> obtenerPorRfc(String rfc);

    Optional<Guardia> obtenerPorIdUsuario(Integer idUsuario);
}
