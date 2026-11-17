package com.sps.security.dao;

import com.sps.security.model.telemetry.Alarma;

import java.util.List;
import java.time.LocalDateTime;
import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;

public interface AlarmaDAO extends CrudDAO<Alarma, Integer>{
    List<Alarma> obtenerPorDispositivo(Integer idDispositivo);
    List<Alarma> obtenerFiltradas(LocalDateTime desde, LocalDateTime hasta,
                                  Integer idDispositivo,
                                  NivelPrioridad prioridad,
                                  EstadoAlarma estado);
}
