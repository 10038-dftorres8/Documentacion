package com.banquito.Documentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <- opcional pero recomendable

public class ClienteDTO {
  private String id;
  private String tipoIdentificacion;
  private String numeroIdentificacion;
  private String nombres;
  private String apellidos;
  private String correoElectronico;
  private String telefono;
  // ...otros campos si quieres
}
