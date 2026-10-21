package com.sps.security.model.telemetry;

import com.sps.security.model.enums.EstadoAlarma;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Alarma {
    private Integer idAlarma;
    private Integer idSensor;
    private LocalDateTime fechaHora;
    private EstadoAlarma estadoActual;
    private String ubicacion;
    private String tipoAlarma;

    public Alarma(Integer idAlarma,
                  Integer idSensor,
                  LocalDateTime fechaHora,
                  EstadoAlarma estadoActual,
                  String ubicacion,
                  String tipoAlarma) {
        this.idAlarma = idAlarma;
        this.idSensor = idSensor;
        this.fechaHora = fechaHora;
        this.estadoActual = estadoActual;
        this.ubicacion = ubicacion;
        this.tipoAlarma = tipoAlarma;
    }

    public Integer getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(Integer idAlarma) {
        this.idAlarma = idAlarma;
    }

    public Integer getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(Integer idSensor) {
        this.idSensor = idSensor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoAlarma getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(EstadoAlarma estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoAlarma() {
        return tipoAlarma;
    }

    public void setTipoAlarma(String tipoAlarma) {
        this.tipoAlarma = tipoAlarma;
    }
}
