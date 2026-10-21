package com.sps.security.model.supervised;

public abstract class ElementoSupervisado {
    private Integer idElemento;
    private Integer idCliente;
    private String tipoElemento;
    private Boolean estadoActivo;

    public ElementoSupervisado(Integer idElemento,
                               Integer idCliente,
                               String tipoElemento,
                               Boolean estadoActivo) {
        this.idElemento = idElemento;
        this.idCliente = idCliente;
        this.tipoElemento = tipoElemento;
        this.estadoActivo = estadoActivo;
    }

    public Integer getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Integer idElemento) {
        this.idElemento = idElemento;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }
}
