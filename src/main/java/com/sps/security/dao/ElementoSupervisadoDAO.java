package com.sps.security.dao;

import com.sps.security.model.supervised.ElementoSupervisado;

import java.util.List;

public interface ElementoSupervisadoDAO extends CrudDAO<ElementoSupervisado, Integer>{
    List<ElementoSupervisado> obtenerPorCliente(Integer idCliente);
}
