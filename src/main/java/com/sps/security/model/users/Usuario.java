package com.sps.security.model.users;

import java.util.Objects;

public class Usuario {
    private Integer idUsuario;
    private Integer idRol;
    private String username;
    private String email;
    private String contraseña;
    private Boolean estadoActivo;

    public Usuario(Integer idUsuario,
                   Integer idRol,
                   String username,
                   String email,
                   String contraseña,
                   Boolean estadoActivo) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.username = username;
        this.email = email;
        this.contraseña = contraseña;
        this.estadoActivo = estadoActivo;
    }

    public Usuario() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return Objects.equals(getIdUsuario(), usuario.getIdUsuario()) && Objects.equals(getIdRol(), usuario.getIdRol()) && Objects.equals(getUsername(), usuario.getUsername()) && Objects.equals(getEmail(), usuario.getEmail()) && Objects.equals(getContraseña(), usuario.getContraseña()) && Objects.equals(getEstadoActivo(), usuario.getEstadoActivo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUsuario(), getIdRol(), getUsername(), getEmail(), getContraseña(), getEstadoActivo());
    }
}
