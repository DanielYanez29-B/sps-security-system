
package com.sps.security.dao;

import com.sps.security.model.supervised.ElementoSupervisado;
import java.util.List;

public interface ElementoSupervisadoDAO extends CrudDAO<ElementoSupervisado, Integer> {

    List<ElementoSupervisado> obtenerPorServicio(Integer idServicio);

}
