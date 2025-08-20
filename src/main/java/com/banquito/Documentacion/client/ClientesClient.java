package com.banquito.Documentacion.client;

import com.banquito.Documentacion.dto.PersonaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientesClient", url = "${cliente.service.url}")
public interface ClientesClient {
  /**
   * GET /v1/clientes/personas/{tipoIdentificacion}/{numeroIdentificacion}
   */
  @GetMapping("/api/clientes/v1/clientes/personas/{tipoIdentificacion}/{numeroIdentificacion}")
  PersonaResponseDTO consultarPersonaPorIdentificacion(
    @PathVariable("tipoIdentificacion") String tipoIdentificacion,
    @PathVariable("numeroIdentificacion") String numeroIdentificacion
  );
}
