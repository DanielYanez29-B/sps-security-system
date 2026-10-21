package com.sps.security.model.telemetry;

import com.sps.security.model.enums.TipoInstalacion;

import java.time.LocalDate;

public class Dispositivo {
    private Integer idDispositivo;
    private Integer idElemento;
    private TipoInstalacion tipoInstalacion;
    private String numSerie;
    private String fabricante;
    private LocalDate fechaInstalacion;
    private Boolean estadoActividad;

    public Dispositivo(Integer idDispositivo,
                       Integer idElemento,
                       TipoInstalacion tipoInstalacion,
                       String numSerie,
                       String fabricante,
                       LocalDate fechaInstalacion,
                       Boolean estadoActividad) {
        this.idDispositivo = idDispositivo;
        this.idElemento = idElemento;
        this.tipoInstalacion = tipoInstalacion;
        this.numSerie = numSerie;
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

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public TipoInstalacion getTipoInstalacion() {
        return tipoInstalacion;
    }

    public void setTipoInstalacion(TipoInstalacion tipoInstalacion) {
        this.tipoInstalacion = tipoInstalacion;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
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
