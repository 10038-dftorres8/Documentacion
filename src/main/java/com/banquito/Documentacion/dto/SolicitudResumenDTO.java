
package com.banquito.Documentacion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SolicitudResumenDTO {
    @JsonProperty("id_solicitud")
    private Long idSolicitud;

    @JsonProperty("cedula_solicitante")
    private String cedulaSolicitante;

    @JsonProperty("nombres_solicitante")
    private String nombresSolicitante;

    @JsonProperty("precio_final_vehiculo")
    private BigDecimal precioFinalVehiculo;

    @JsonProperty("monto_aprobado")
    private BigDecimal montoAprobado;

    @JsonProperty("plazo_final_meses")
    private Integer plazoFinalMeses;

    @JsonProperty("tasa_efectiva_anual")
    private BigDecimal tasaEfectivaAnual;

    // Getters y setters en camelCase

    public Long getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Long idSolicitud) { this.idSolicitud = idSolicitud; }

    public BigDecimal getPrecioFinalVehiculo() { return precioFinalVehiculo; }
    public void setPrecioFinalVehiculo(BigDecimal precioFinalVehiculo) { this.precioFinalVehiculo = precioFinalVehiculo; }

    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public void setMontoAprobado(BigDecimal montoAprobado) { this.montoAprobado = montoAprobado; }

    public Integer getPlazoFinalMeses() { return plazoFinalMeses; }
    public void setPlazoFinalMeses(Integer plazoFinalMeses) { this.plazoFinalMeses = plazoFinalMeses; }

    public BigDecimal getTasaEfectivaAnual() { return tasaEfectivaAnual; }
    public void setTasaEfectivaAnual(BigDecimal tasaEfectivaAnual) { this.tasaEfectivaAnual = tasaEfectivaAnual; }

    public String getCedulaSolicitante() {
        return cedulaSolicitante;
    }

    public void setCedulaSolicitante(String cedulaSolicitante) {
        this.cedulaSolicitante = cedulaSolicitante;
    }

    public String getNombresSolicitante() {
        return nombresSolicitante;
    }

    public void setNombresSolicitante(String nombresSolicitante) {
        this.nombresSolicitante = nombresSolicitante;
    }
}
