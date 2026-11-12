package com.sps.security.model;

import java.time.LocalDate;

public class Guardia {
    private int idGuardia;
    private Integer idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaIngreso;
    private String rfc;
    private boolean activo;

    public Guardia(int idGuardia, String nombre, String apellido, String telefono, boolean activo) {}

    public Guardia(int idGuardia, Integer idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, LocalDate fechaIngreso, String rfc, boolean activo) {
        this.idGuardia = idGuardia;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaIngreso = fechaIngreso;
        this.rfc = rfc;
        this.activo = activo;
    }

    public int getIdGuardia() { return idGuardia; }
    public void setIdGuardia(int idGuardia) { this.idGuardia = idGuardia; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}