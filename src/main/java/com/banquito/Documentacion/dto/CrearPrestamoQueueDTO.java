package com.banquito.Documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearPrestamoQueueDTO {
    private String idCliente;
    private String idPrestamo;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
}
