package com.sps.security.model.telemetry;

public class Sensor {
    private Integer idSensor;
    private Integer idDispositivo;
    private String tipoSensor;
    private String ubicacion;
    private Boolean estadoActividad;

    public Sensor(Integer idSensor,
                  Integer idDispositivo,
                  String tipoSensor,
                  String ubicacion,
                  Boolean estadoActividad) {
        this.idSensor = idSensor;
        this.idDispositivo = idDispositivo;
        this.tipoSensor = tipoSensor;
        this.ubicacion = ubicacion;
        this.estadoActividad = estadoActividad;
    }

    public Integer getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(Integer idSensor) {
        this.idSensor = idSensor;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Boolean getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(Boolean estadoActividad) {
        this.estadoActividad = estadoActividad;
    }
}
