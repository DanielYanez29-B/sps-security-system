package com.sps.security.dao;

import com.sps.security.model.operative.Jornada;

import java.util.Optional;

public interface JornadaDAO extends CrudDAO<Jornada, Integer> {
    Optional<Jornada> obtenerPorNombre(String nombreTurno);

}
