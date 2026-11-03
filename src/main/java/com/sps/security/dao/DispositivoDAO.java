package com.sps.security.dao;

import com.sps.security.model.telemetry.Dispositivo;

import java.util.Optional;

public interface DispositivoDAO extends CrudDAO<Dispositivo, Integer>{
    Optional<Dispositivo> obtenerPorNumSerie(String numSerie);
}
