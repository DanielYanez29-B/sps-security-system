package com.sps.security.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, id> {
    boolean guardar(T entidad);
    Optional<T> obtenerPorId(id id);
    List<T> obtenerTodos();
    boolean actualizar(T entidad);
    boolean eliminar(T entidad);
}
