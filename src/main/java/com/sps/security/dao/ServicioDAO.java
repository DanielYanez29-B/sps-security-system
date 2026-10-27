package com.sps.security.dao;

import com.sps.security.model.operative.Servicio;

import java.util.List;

public interface ServicioDAO extends CrudDAO<Servicio, Integer>{
    List<Servicio> obtenerServiciosActivos();
}
