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
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.estado = estado;
        this.cp = cp;
    }

    public Integer getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(Integer idcliente) {
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
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;

        Cliente cliente = (Cliente) o;

        return Objects.equals(idcliente, cliente.idcliente)
                && Objects.equals(nombre, cliente.nombre)
                && Objects.equals(rfc, cliente.rfc)
                && Objects.equals(telefono, cliente.telefono)
                && Objects.equals(email, cliente.email)
                && Objects.equals(calle, cliente.calle)
                && Objects.equals(num_ext, cliente.num_ext)
                && Objects.equals(colonia, cliente.colonia)
                && Objects.equals(ciudad, cliente.ciudad)
                && Objects.equals(estado, cliente.estado)
                && Objects.equals(cp, cliente.cp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                idcliente,
                nombre,
                rfc,
                telefono,
                email,
                calle,
                num_ext,
                colonia,
                ciudad,
                estado,
                cp
        );
    }
}
