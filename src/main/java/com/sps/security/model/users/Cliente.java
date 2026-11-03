package com.sps.security.model.users;

import java.util.Objects;

public class Cliente {
    private Integer idcliente;
    private String nombre;
    private String rfc;
    private String telefono;
    private String email;
    private String calle;
    private String num_ext;
    private String num_int;
    private String colonia;
    private String ciudad;
    private String estado;
    private String cp;

    public Cliente(Integer idcliente,
                   String nombre,
                   String rfc,
                   String telefono,
                   String email,
                   String calle,
                   String num_ext,
                   String num_int,
                   String colonia,
                   String ciudad,
                   String estado,
                   String cp) {

        this.idcliente = idcliente;
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.email = email;
        this.calle = calle;
        this.num_ext = num_ext;
        this.num_int = num_int;
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.estado = estado;
        this.cp = cp;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    private void setIdcliente(Integer idcliente) {
        this.idcliente = idcliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNum_ext() {
        return num_ext;
    }

    public void setNum_ext(String num_ext) {
        this.num_ext = num_ext;
    }

    public String getNum_int() {
        return num_int;
    }

    public void setNum_int(String num_int) {
        this.num_int = num_int;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(getIdcliente(), cliente.getIdcliente()) && Objects.equals(getNombre(), cliente.getNombre()) && Objects.equals(getRfc(), cliente.getRfc()) && Objects.equals(getTelefono(), cliente.getTelefono()) && Objects.equals(getEmail(), cliente.getEmail()) && Objects.equals(getCalle(), cliente.getCalle()) && Objects.equals(getNum_ext(), cliente.getNum_ext()) && Objects.equals(getNum_int(), cliente.getNum_int()) && Objects.equals(getColonia(), cliente.getColonia()) && Objects.equals(getCiudad(), cliente.getCiudad()) && Objects.equals(getEstado(), cliente.getEstado()) && Objects.equals(getCp(), cliente.getCp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdcliente(), getNombre(), getRfc(), getTelefono(), getEmail(), getCalle(), getNum_ext(), getNum_int(), getColonia(), getCiudad(), getEstado(), getCp());
    }
}
