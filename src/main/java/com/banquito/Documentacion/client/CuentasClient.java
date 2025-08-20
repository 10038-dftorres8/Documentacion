// src/main/java/com/banquito/Documentacion/client/CuentasClient.java
package com.banquito.Documentacion.client;

import com.banquito.Documentacion.dto.CuentaClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cuentasClient", url = "${cuentas.service.url}")
public interface CuentasClient {

    // Soporta idCliente o cédula (tu MS admite ambos)
    @GetMapping("/v1/cuentas-clientes/cliente/{idCliente}")
    List<CuentaClienteDTO> obtenerCuentasPorCliente(@PathVariable("idCliente") String idCliente);
}
