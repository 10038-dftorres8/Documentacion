package com.banquito.Documentacion.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <- opcional pero recomendable

public class PrestamoClienteDTO {
  private Integer id;
  private String idCliente;
  private String idPrestamo;
  private BigDecimal montoSolicitado;
  private Integer plazoMeses;
  private String estado;

}
