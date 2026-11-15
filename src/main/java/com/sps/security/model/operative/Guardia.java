package com.sps.security.model.operative;

import java.time.LocalDate;


public class Guardia {
    private int idGuardia;
    private Integer idUsuario;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String rfc;
    private LocalDate fechaIngreso;
    private boolean estatusActivo;

    public Guardia() {
        this.estatusActivo = true;
    }

    public Guardia(int idGuardia, Integer idUsuario, String nombres,
                   String apellidoPaterno, String apellidoMaterno,
                   LocalDate fechaIngreso, String rfc, boolean estatusActivo) {
        this.idGuardia = idGuardia;
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaIngreso = fechaIngreso;
        this.rfc = rfc;
        this.estatusActivo = estatusActivo;
    }

    public int getIdGuardia() { return idGuardia; }
    public void setIdGuardia(int idGuardia) { this.idGuardia = idGuardia; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public boolean isEstatusActivo() { return estatusActivo; }
    public void setEstatusActivo(boolean estatusActivo) { this.estatusActivo = estatusActivo; }

    @Override
    public String toString() {
        String materno = (apellidoMaterno == null || apellidoMaterno.isBlank()) ? "" : " " + apellidoMaterno;
        return nombres + " " + apellidoPaterno + materno;
    }
}