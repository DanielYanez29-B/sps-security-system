package com.sps.security.dao;

import com.sps.security.model.telemetry.Notificacion;

import java.util.List;

public interface NotificacionDAO extends CrudDAO<Notificacion, Integer>{
    List<Notificacion> obtenerPorAlarma(Integer idAlarma);
    List<Notificacion> obtenerPorUsuario(Integer idUsuario);

    List<Notificacion> obtenerNoLeidasPorUsuario(Integer idUsuario);

    boolean marcarComoLeida(Integer idNotificacion);

    int marcarTodasComoLeidas(Integer idUsuario);
}
