package com.banquito.Documentacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banquito.Documentacion.enums.ContratoVentaEstado;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContratoCompraVentaDTO {

    private Long idContratoVenta;
    private Long idSolicitud;
    private String numeroContrato;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaFirma;
    private BigDecimal precioFinalVehiculo;
    private String rutaArchivoFirmado;
    private ContratoVentaEstado estado;
    private Long version;

}
