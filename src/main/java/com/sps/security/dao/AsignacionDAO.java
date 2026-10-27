package com.sps.security.dao;

import com.sps.security.model.operative.Asignacion;

import java.util.List;

public interface AsignacionDAO extends CrudDAO<Asignacion, Integer>{
    List<Asignacion> obtenerAsignacionesPorGuardia(Integer idGuardia);
}
