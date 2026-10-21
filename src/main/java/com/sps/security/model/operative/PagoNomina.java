package com.sps.security.model.operative;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class PagoNomina {

    private Integer idPago;
    private Integer idGuardia;
    private LocalDate fechaPago;
    private String periodoPago;
    private BigDecimal montoBase;
    private BigDecimal montoIncentivos;
    private BigDecimal deducciones;
    private BigDecimal montoNeto;

    // Constructor vacío (Requerido por JavaBeans)
    public PagoNomina() {
        this.montoBase = BigDecimal.ZERO;
        this.montoIncentivos = BigDecimal.ZERO;
        this.deducciones = BigDecimal.ZERO;
        this.montoNeto = BigDecimal.ZERO;
    }

    // Constructor completo
    public PagoNomina(Integer idPago, Integer idGuardia, LocalDate fechaPago, String periodoPago,
                      BigDecimal montoBase, BigDecimal montoIncentivos, BigDecimal deducciones) {
        this.idPago = idPago;
        this.idGuardia = idGuardia;
        this.fechaPago = fechaPago;
        this.periodoPago = periodoPago;
        this.montoBase = montoBase != null ? montoBase : BigDecimal.ZERO;
        this.montoIncentivos = montoIncentivos != null ? montoIncentivos : BigDecimal.ZERO;
        this.deducciones = deducciones != null ? deducciones : BigDecimal.ZERO;
        recalcularMontoNeto();
    }

    // Método helper para sincronizar el cálculo financiero en memoria
    public void recalcularMontoNeto() {
        BigDecimal base = this.montoBase != null ? this.montoBase : BigDecimal.ZERO;
        BigDecimal inc = this.montoIncentivos != null ? this.montoIncentivos : BigDecimal.ZERO;
        BigDecimal ded = this.deducciones != null ? this.deducciones : BigDecimal.ZERO;
        this.montoNeto = base.add(inc).subtract(ded);
    }

    // Getters y Setters
    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(Integer idGuardia) {
        this.idGuardia = idGuardia;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getPeriodoPago() {
        return periodoPago;
    }

    public void setPeriodoPago(String periodoPago) {
        this.periodoPago = periodoPago;
    }

    public BigDecimal getMontoBase() {
        return montoBase;
    }

    public void setMontoBase(BigDecimal montoBase) {
        this.montoBase = montoBase;
        recalcularMontoNeto();
    }

    public BigDecimal getMontoIncentivos() {
        return montoIncentivos;
    }

    public void setMontoIncentivos(BigDecimal montoIncentivos) {
        this.montoIncentivos = montoIncentivos != null ? montoIncentivos : BigDecimal.ZERO;
        recalcularMontoNeto();
    }

    public BigDecimal getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(BigDecimal deducciones) {
        this.deducciones = deducciones != null ? deducciones : BigDecimal.ZERO;
        recalcularMontoNeto();
    }

    public BigDecimal getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(BigDecimal montoNeto) {
        this.montoNeto = montoNeto;
    }

    // Implementación de equals y hashCode basados exclusivamente en la PK (idPago)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoNomina that = (PagoNomina) o;
        return idPago != null && idPago.equals(that.idPago);
    }

    @Override
    public int hashCode() {
        return idPago != null ? Objects.hash(idPago) : super.hashCode();
    }

    @Override
    public String toString() {
        return "PagoNomina{" +
                "idPago=" + idPago +
                ", idGuardia=" + idGuardia +
                ", fechaPago=" + fechaPago +
                ", periodoPago='" + periodoPago + '\'' +
                ", montoNeto=" + montoNeto +
                '}';
    }
}
