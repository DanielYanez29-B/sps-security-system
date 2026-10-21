package com.sps.security.model.supervised;

public class Persona extends ElementoSupervisado {
    private String nombre;
    private String parentesco;
    private String telefonoContacto;

    public Persona(Integer idElemento,
                   Integer idCliente,
                   String tipoElemento,
                   Boolean estadoActivo,
                   String nombre,
                   String parentesco,
                   String telefonoContacto) {
        super(idElemento, idCliente, tipoElemento, estadoActivo);
        this.nombre = nombre;
        this.parentesco = parentesco;
        this.telefonoContacto = telefonoContacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
}
