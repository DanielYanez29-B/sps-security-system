package com.sps.security.model.telemetry;

import java.time.LocalDate;
import java.time.LocalTime;

public class Supervision {
    private Integer idSupervision;
    private Integer idGuardia;
    private Integer idElemento;
    private LocalTime fechaHora;
    private String detallesObservacion;

    public Supervision(Integer idSupervision,
                       Integer idGuardia,
                       Integer idElemento,
                       LocalTime fechaHora,
                       String detallesObservacion) {
        this.idSupervision = idSupervision;
        this.idGuardia = idGuardia;
        this.idElemento = idElemento;
        this.fechaHora = fechaHora;
        this.detallesObservacion = detallesObservacion;
    }

    public Integer getIdSupervision() {
        return idSupervision;
    }

    public void setIdSupervision(Integer idSupervision) {
        this.idSupervision = idSupervision;
    }

    public Integer getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(Integer idGuardia) {
        this.idGuardia = idGuardia;
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public LocalTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDetallesObservacion() {
        return detallesObservacion;
    }

    public void setDetallesObservacion(String detallesObservacion) {
        this.detallesObservacion = detallesObservacion;
    }
}
