package com.sps.security.model.operative;

import java.time.LocalDate;
import java.util.Objects;

public class Guardia {
    private Integer idGuardia;
    private Integer idCliente;
    private Integer idUsuario;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDate fechaIngreso;
    private Boolean estatusActivo;

    public Guardia(Integer idGuardia,
                   Integer idCliente,
                   Integer idUsuario,
                   String nombres,
                   String apellidoPaterno,
                   String apellidoMaterno,
                   LocalDate fechaIngreso,
                   Boolean estatusActivo) {
        this.idGuardia = idGuardia;
        this.idCliente = idCliente;
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaIngreso = fechaIngreso;
        this.estatusActivo = estatusActivo;
    }

    public Integer getIdGuardia() {
        return idGuardia;
    }

    private void setIdGuardia(Integer idGuardia) {
        this.idGuardia = idGuardia;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Boolean getEstatusActivo() {
        return estatusActivo;
    }

    public void setEstatusActivo(Boolean estatusActivo) {
        this.estatusActivo = estatusActivo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Guardia guardia = (Guardia) o;
        return Objects.equals(getIdGuardia(), guardia.getIdGuardia()) && Objects.equals(getIdCliente(), guardia.getIdCliente()) && Objects.equals(getIdUsuario(), guardia.getIdUsuario()) && Objects.equals(getNombres(), guardia.getNombres()) && Objects.equals(getApellidoPaterno(), guardia.getApellidoPaterno()) && Objects.equals(getApellidoMaterno(), guardia.getApellidoMaterno()) && Objects.equals(getFechaIngreso(), guardia.getFechaIngreso()) && Objects.equals(getEstatusActivo(), guardia.getEstatusActivo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdGuardia(), getIdCliente(), getIdUsuario(), getNombres(), getApellidoPaterno(), getApellidoMaterno(), getFechaIngreso(), getEstatusActivo());
    }
}
