// src/main/java/com/banquito/Documentacion/dto/TransferRequest.java
package com.banquito.Documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransferRequest {
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private String tipoTransaccion;  // "TRANSFERENCIA"
    private BigDecimal monto;
    private String descripcion;      // aquí pondremos el número de solicitud
}
