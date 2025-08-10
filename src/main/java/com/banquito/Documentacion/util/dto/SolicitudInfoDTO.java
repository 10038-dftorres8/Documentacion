package com.banquito.Documentacion.util.dto;

import lombok.Data;

@Data
public class SolicitudInfoDTO {
    private Long idSolicitud;
    private String estado;
    private Double capacidadPago;
    private String nivelRiesgo;
    private String decisionAutomatica;
    private String observaciones;
    private String justificacionAnalista;
}
