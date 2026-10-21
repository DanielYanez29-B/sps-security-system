package com.sps.security.model.telemetry;

public class Autoridad {
    private Integer idAutoridad;
    private String nombre;
    private String dependencia;
    private String telefonoContacto;
    private String canalizacionEmergencia;

    public Autoridad(Integer idAutoridad,
                     String nombre,
                     String dependencia,
                     String telefonoContacto,
                     String canalizacionEmergencia) {
        this.idAutoridad = idAutoridad;
        this.nombre = nombre;
        this.dependencia = dependencia;
        this.telefonoContacto = telefonoContacto;
        this.canalizacionEmergencia = canalizacionEmergencia;
    }

    public Integer getIdAutoridad() {
        return idAutoridad;
    }

    public void setIdAutoridad(Integer idAutoridad) {
        this.idAutoridad = idAutoridad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getCanalizacionEmergencia() {
        return canalizacionEmergencia;
    }

    public void setCanalizacionEmergencia(String canalizacionEmergencia) {
        this.canalizacionEmergencia = canalizacionEmergencia;
    }
}
