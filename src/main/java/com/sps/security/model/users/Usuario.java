package com.sps.security.model.users;

import java.util.Objects;

public class Usuario {
    private Integer idUsuario;
    private Integer idRol;
    private String username;
    private String email;
    private String contrasena;
    private Boolean estadoActivo;

    public Usuario() {
        this.estadoActivo = true;
    }

    public Usuario(Integer idUsuario, Integer idRol, String username, String email, String contrasena, Boolean estadoActivo) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.username = username;
        this.email = email;
        this.contrasena = contrasena;
        this.estadoActivo = estadoActivo != null ? estadoActivo : true;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Boolean getEstadoActivo() { return estadoActivo; }
    public void setEstadoActivo(Boolean estadoActivo) { this.estadoActivo = estadoActivo; }

    // equals() y hashCode() exclusivamente basados en la PK idUsuario
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return idUsuario != null && idUsuario.equals(usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return idUsuario != null ? Objects.hash(idUsuario) : super.hashCode();
    }
    public Boolean isEstadoActivo() {
        return estadoActivo;
    }
}