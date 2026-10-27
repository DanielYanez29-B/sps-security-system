package com.sps.security.dao;

import com.sps.security.model.telemetry.Alarma;

import java.util.Optional;

public interface AlarmaDAO extends CrudDAO<Alarma, Integer>{
    Optional<Alarma> obtenerPorDispositivo(Integer idDispositivo);
}
