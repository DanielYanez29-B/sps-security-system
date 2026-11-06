package com.sps.security.model.access;

public class Permiso {
    private Integer idPermiso;
    private String clavePermiso;
    private String descripcion;

    public Permiso(Integer idPermiso, String clavePermiso, String descripcion) {
        this.idPermiso = idPermiso;
        this.clavePermiso = clavePermiso;
        this.descripcion = descripcion;
    }
    public Permiso(){};

    public Integer getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Integer idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getClavePermiso() {
        return clavePermiso;
    }

    public void setClavePermiso(String clavePermiso) {
        this.clavePermiso = clavePermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}