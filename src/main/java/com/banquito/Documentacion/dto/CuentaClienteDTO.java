package com.banquito.Documentacion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)   // 👈 clave
public class CuentaClienteDTO {
    private Long id;            // opcional, por si viene
    private String idCliente;   // puede venir cédula o id; no lo usamos
    private String numeroCuenta;
    private String estado;      // "ACTIVO", etc.
}
