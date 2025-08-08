package com.banquito.Documentacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.banquito.Documentacion.dto.CrearPrestamoRequest;
import com.banquito.Documentacion.dto.PrestamoClienteDTO;

@FeignClient(name = "prestamoClient", url = "${prestamo.service.url}")
public interface PrestamoClient {
  @PostMapping("/api/prestamos/v1/prestamos-clientes")
  PrestamoClienteDTO crearPrestamo(@RequestBody CrearPrestamoRequest req);

  @GetMapping("/api/prestamos/v1/prestamos-clientes/buscar")
  PrestamoClienteDTO buscarPrestamo(@RequestParam String idCliente, @RequestParam String idPrestamo);

  @PutMapping("/api/prestamos/v1/prestamos-clientes/{id}/estado")
  PrestamoClienteDTO actualizarEstado(@PathVariable Integer id,
      @RequestParam String estado);

}
