package com.sps.security.model.telemetry;

import java.time.LocalDateTime;

public class Notificacion {
    private Integer idNotificacion;
    private Integer idEmergencia;
    private Integer idAlarma;
    private LocalDateTime fechaHora;
    private String notificacion;
    private String estadoEnvio;

    public Notificacion(Integer idNotificacion,
                        Integer idEmergencia,
                        Integer idAlarma,
                        LocalDateTime fechaHora,
                        String notificacion,
                        String estadoEnvio) {
        this.idNotificacion = idNotificacion;
        this.idEmergencia = idEmergencia;
        this.idAlarma = idAlarma;
        this.fechaHora = fechaHora;
        this.notificacion = notificacion;
        this.estadoEnvio = estadoEnvio;
    }

    public Integer getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Integer getIdEmergencia() {
        return idEmergencia;
    }

    public void setIdEmergencia(Integer idEmergencia) {
        this.idEmergencia = idEmergencia;
    }

    public Integer getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(Integer idAlarma) {
        this.idAlarma = idAlarma;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }
}
