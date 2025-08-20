package com.banquito.Documentacion.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <- opcional pero recomendable
public class DetalleSolicitudResponseDTO {
  private Long idSolicitud;
  private String numeroSolicitud;

  // Nuevos campos:
  private String cedulaSolicitante;
  private String nombresSolicitante;

  private String placaVehiculo;
  private String marcaVehiculo;
  private String modeloVehiculo;
  private Integer anioVehiculo;
  private BigDecimal valorVehiculo;

  private String idPrestamo;
  private BigDecimal montoSolicitado;
  private Integer plazoMeses;

  private String estado;

  // Campos adicionales para concesionario y vendedor
  private String rucConcesionario;
  private String razonSocialConcesionario;
  private String direccionConcesionario;
  private String cedulaVendedor;
  private String nombreVendedor;
  private String telefonoVendedor;
  private String emailVendedor;

  // Getters y setters para los nuevos campos adicionales
  public String getRucConcesionario() { return rucConcesionario; }
  public void setRucConcesionario(String rucConcesionario) { this.rucConcesionario = rucConcesionario; }
  public String getRazonSocialConcesionario() { return razonSocialConcesionario; }
  public void setRazonSocialConcesionario(String razonSocialConcesionario) { this.razonSocialConcesionario = razonSocialConcesionario; }
  public String getDireccionConcesionario() { return direccionConcesionario; }
  public void setDireccionConcesionario(String direccionConcesionario) { this.direccionConcesionario = direccionConcesionario; }
  public String getCedulaVendedor() { return cedulaVendedor; }
  public void setCedulaVendedor(String cedulaVendedor) { this.cedulaVendedor = cedulaVendedor; }
  public String getNombreVendedor() { return nombreVendedor; }
  public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }
  public String getTelefonoVendedor() { return telefonoVendedor; }
  public void setTelefonoVendedor(String telefonoVendedor) { this.telefonoVendedor = telefonoVendedor; }
  public String getEmailVendedor() { return emailVendedor; }
  public void setEmailVendedor(String emailVendedor) { this.emailVendedor = emailVendedor; }
}
