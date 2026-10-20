package com.sps.security.model.users;

import java.util.Objects;

public class Usuario {
    private Integer idUsuario;
    private Integer idRol;
    private Integer idCliente;
    private String email;
    private String contraseña;
    private Boolean estadoActivo;

    public Usuario(Integer idUsuario,
                   Integer idRol,
                   Integer idCliente,
                   String email,
                   String contraseña,
                   Boolean estadoActivo) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.idCliente = idCliente;
        this.email = email;
        this.contraseña = contraseña;
        this.estadoActivo = estadoActivo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getIdUsuario(), usuario.getIdUsuario()) && Objects.equals(getIdRol(), usuario.getIdRol()) && Objects.equals(getIdCliente(), usuario.getIdCliente()) && Objects.equals(getEmail(), usuario.getEmail()) && Objects.equals(getContraseña(), usuario.getContraseña()) && Objects.equals(getEstadoActivo(), usuario.getEstadoActivo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUsuario(), getIdRol(), getIdCliente(), getEmail(), getContraseña(), getEstadoActivo());
    }
}
