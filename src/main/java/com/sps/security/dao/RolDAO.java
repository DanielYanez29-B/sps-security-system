package com.sps.security.dao;

import com.sps.security.model.access.Rol;

import java.util.Optional;

public interface RolDAO extends CrudDAO<Rol, Integer>{
    Optional<Rol> buscarPorNombre(String nombre);
}
