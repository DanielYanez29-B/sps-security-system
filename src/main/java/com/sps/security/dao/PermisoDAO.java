package com.sps.security.dao;

import com.sps.security.model.access.Permiso;

import java.util.List;

public interface PermisoDAO extends CrudDAO<Permiso, Integer>{
    List<Permiso> obtenerPorRolId(Integer idRol);
}
