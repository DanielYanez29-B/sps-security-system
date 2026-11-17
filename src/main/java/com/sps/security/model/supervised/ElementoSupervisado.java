
package com.sps.security.model.supervised;

import com.sps.security.model.enums.TipoElemento;

public class ElementoSupervisado {

    private Integer idElemento;
    private Integer idServicio;
    private TipoElemento tipoElemento;
    private String identificador;
    private String descripcion;
    private Boolean estadoActivo;

    public ElementoSupervisado(Integer idElemento,
                               Integer idServicio,
                               TipoElemento tipoElemento,
                               String identificador,
                               String descripcion,
                               Boolean estadoActivo) {
        this.idElemento = idElemento;
        this.idServicio = idServicio;
        this.tipoElemento = tipoElemento;
        this.identificador = identificador;
        this.descripcion = descripcion;
        this.estadoActivo = estadoActivo;
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public TipoElemento getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(TipoElemento tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
}
