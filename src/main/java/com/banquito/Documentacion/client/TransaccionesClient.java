// src/main/java/com/banquito/Documentacion/client/TransaccionesClient.java
package com.banquito.Documentacion.client;

import com.banquito.Documentacion.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaccionesClient", url = "${cuentas.service.url}")
public interface TransaccionesClient {

    @PostMapping("/v1/transacciones/transferencia")
    void crearTransferencia(@RequestBody TransferRequest request);
}
