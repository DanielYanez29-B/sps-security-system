
package com.sps.security.model.operative;

import java.time.LocalDate;

public class Servicio {

    private Integer idServicio;
    private Integer idCliente;
    private String nombreServicio;
    private String direccion;
    private LocalDate fechaInicio;
    private String estado;

    public Servicio(Integer idServicio,
                    Integer idCliente,
                    String nombreServicio,
                    String direccion,
                    LocalDate fechaInicio,
                    String estado) {

        this.idServicio = idServicio;
        this.idCliente = idCliente;
        this.nombreServicio = nombreServicio;
        this.direccion = direccion;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
