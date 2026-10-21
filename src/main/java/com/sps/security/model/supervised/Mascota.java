package com.sps.security.model.supervised;

public class Mascota extends ElementoSupervisado {
    private String nombre;
    private String especie;
    private String raza;
    private String color;

    public Mascota(Integer idElemento,
                   Integer idCliente,
                   String tipoElemento,
                   Boolean estadoActivo,
                   String nombre,
                   String especie,
                   String raza,
                   String color) {
        super(idElemento, idCliente, tipoElemento, estadoActivo);
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
