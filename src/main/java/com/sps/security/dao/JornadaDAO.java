package com.sps.security.dao;

import com.sps.security.model.Jornada;
import java.util.List;
import java.util.Optional;

public interface JornadaDAO extends CrudDAO<Jornada, Integer> {
    Optional<Jornada> obtenerPorNombre(String nombreTurno);
}