package com.sps.security.model.telemetry;

import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import java.time.LocalDateTime;

public class Alarma {
    private Integer idAlarma;
    private Integer idDispositivo;
    private LocalDateTime fechaHora;
    private NivelPrioridad nivelPrioridad;
    private String tipoAlarma;
    private EstadoAlarma estado;
    private String descripcion;

    public Alarma(Integer idAlarma,
                  Integer idDispositivo,
                  LocalDateTime fechaHora,
                  NivelPrioridad nivelPrioridad,
                  String tipoAlarma,
                  EstadoAlarma estado,
                  String descripcion) {
        this.idAlarma = idAlarma;
        this.idDispositivo = idDispositivo;
        this.fechaHora = fechaHora;
        this.nivelPrioridad = nivelPrioridad;
        this.tipoAlarma = tipoAlarma;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public Integer getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(Integer idAlarma) {
        this.idAlarma = idAlarma;
    }

    public Integer getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public NivelPrioridad getNivelPrioridad() {
        return nivelPrioridad;
    }

    public void setNivelPrioridad(NivelPrioridad nivelPrioridad) {
        this.nivelPrioridad = nivelPrioridad;
    }

    public EstadoAlarma getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlarma estado) {
        this.estado = estado;
    }
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoAlarma() {
        return tipoAlarma;
    }

    public void setTipoAlarma(String tipoAlarma) {
        this.tipoAlarma = tipoAlarma;
    }
}
