package com.sps.security.model.telemetry;

import java.time.LocalDateTime;

public class Emergencia {
    private Integer idEmergencia;
    private Integer idAlarma;
    private LocalDateTime fechaHora;
    private String detallesEmergencia;
    private String estadoAtencion;
    private String conductaTomada;

    public Emergencia(Integer idEmergencia,
                      Integer idAlarma,
                      LocalDateTime fechaHora,
                      String detallesEmergencia,
                      String estadoAtencion,
                      String conductaTomada) {
        this.idEmergencia = idEmergencia;
        this.idAlarma = idAlarma;
        this.fechaHora = fechaHora;
        this.detallesEmergencia = detallesEmergencia;
        this.estadoAtencion = estadoAtencion;
        this.conductaTomada = conductaTomada;
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

    public String getDetallesEmergencia() {
        return detallesEmergencia;
    }

    public void setDetallesEmergencia(String detallesEmergencia) {
        this.detallesEmergencia = detallesEmergencia;
    }

    public String getEstadoAtencion() {
        return estadoAtencion;
    }

    public void setEstadoAtencion(String estadoAtencion) {
        this.estadoAtencion = estadoAtencion;
    }

    public String getConductaTomada() {
        return conductaTomada;
    }

    public void setConductaTomada(String conductaTomada) {
        this.conductaTomada = conductaTomada;
    }
}
