package com.sps.security.model.supervised;

import com.sps.security.model.enums.TipoElemento;

public class Vehiculo extends ElementoSupervisado {
    private String placas;
    private String marca;
    private String modelo;
    private String color;
    private String numSerie;

    public Vehiculo(Integer idElemento,
                    Integer idCliente,
                    TipoElemento tipoElemento,
                    Boolean estadoActivo,
                    String placas,
                    String marca,
                    String modelo,
                    String color,
                    String numSerie) {
        super(idElemento, idCliente, tipoElemento, estadoActivo);
        this.placas = placas;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.numSerie = numSerie;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }
}
