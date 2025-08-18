package com.banquito.Documentacion.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <- opcional pero recomendable

public class DetalleSolicitudResponseDTO {
  private Long   idSolicitud;
  private String numeroSolicitud;

    // Nuevos campos:
  private String cedulaSolicitante;
  private String nombresSolicitante;
  private String placaVehiculo;

  private String idPrestamo;
  private BigDecimal montoSolicitado;
  private Integer plazoMeses;

  private String estado;
}
