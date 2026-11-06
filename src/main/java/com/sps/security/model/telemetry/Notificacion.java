package com.sps.security.model.telemetry;

import java.time.LocalDateTime;

public class Notificacion {
    private Integer idNotificacion;
    private Integer idAlarma;
    private Integer idUsuario;
    private LocalDateTime fechaHora;
    private String mensaje;
    private Boolean leido;

    public Notificacion(Integer idNotificacion,
                        Integer idAlarma,
                        Integer idUsuario,
                        LocalDateTime fechaHora,
                        String mensaje,
                        Boolean leido) {
        this.idNotificacion = idNotificacion;
        this.idAlarma = idAlarma;
        this.idUsuario = idUsuario;
        this.fechaHora = fechaHora;
        this.mensaje = mensaje;
        this.leido = leido;
    }

    public Integer getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Integer getIdAlarma() {
        return idAlarma;
    }
    public void setIdAlarma(Integer idAlarma) {
        this.idAlarma = idAlarma;
    }


    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }
}
