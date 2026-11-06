package com.sps.security.dao;

import com.sps.security.model.telemetry.Alarma;

import java.util.List;

public interface AlarmaDAO extends CrudDAO<Alarma, Integer>{
    List<Alarma> obtenerPorDispositivo(Integer idDispositivo);
}
