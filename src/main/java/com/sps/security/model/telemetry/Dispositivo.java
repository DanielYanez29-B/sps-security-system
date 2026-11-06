package com.sps.security.model.telemetry;

import java.time.LocalDate;

public class Dispositivo {
    private Integer idDispositivo;
    private Integer idServicio;
    private String numSerie;
    private String macAddress;
    private String ipAsignada;
    private String fabricante;
    private LocalDate fechaInstalacion;
    private Boolean estadoActividad;

    public Dispositivo(Integer idDispositivo,
                       Integer idServicio,
                       String numSerie,
                       String macAddress,
                       String ipAsignada,
                       String fabricante,
                       LocalDate fechaInstalacion,
                       Boolean estadoActividad) {
        this.idDispositivo = idDispositivo;
        this.idServicio = idServicio;
        this.numSerie = numSerie;
        this.macAddress = macAddress;
        this.ipAsignada = ipAsignada;
        this.fabricante = fabricante;
        this.fechaInstalacion = fechaInstalacion;
        this.estadoActividad = estadoActividad;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public Integer getIdServicio() {
        return idServicio;
    }
    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAsignada() {
        return ipAsignada;
    }

    public void setIpAsignada(String ipAsignada) {
        this.ipAsignada = ipAsignada;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public LocalDate getFechaInstalacion() {
        return fechaInstalacion;
    }

    public void setFechaInstalacion(LocalDate fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }

    public Boolean getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(Boolean estadoActividad) {
        this.estadoActividad = estadoActividad;
    }
}
