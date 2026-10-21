package com.sps.security.model.operative;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asignacion {
    private Integer idAsignacion;
    private Integer idGuardia;
    private Integer idServicio;
    private Integer idJornada;
    private LocalDate fechaAsignacion;

    public Asignacion(Integer idAsignacion,
                      Integer idGuardia,
                      Integer idServicio,
                      Integer idJornada,
                      LocalDate fechaAsignacion) {
        this.idAsignacion = idAsignacion;
        this.idGuardia = idGuardia;
        this.idServicio = idServicio;
        this.idJornada = idJornada;
        this.fechaAsignacion = fechaAsignacion;
    }

    public Integer getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Integer idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public Integer getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(Integer idGuardia) {
        this.idGuardia = idGuardia;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public Integer getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Integer idJornada) {
        this.idJornada = idJornada;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
