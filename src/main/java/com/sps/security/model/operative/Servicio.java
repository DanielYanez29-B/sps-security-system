package com.sps.security.model.operative;

import java.time.LocalDate;

public class Servicio {
    private Integer idServicio;
    private Integer idCliente;
    private String nombreServicio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Servicio(Integer idServicio,
                    Integer idCliente,
                    String nombreServicio,
                    LocalDate fechaInicio,
                    LocalDate fechaFin) {
        this.idServicio = idServicio;
        this.idCliente = idCliente;
        this.nombreServicio = nombreServicio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }


}
