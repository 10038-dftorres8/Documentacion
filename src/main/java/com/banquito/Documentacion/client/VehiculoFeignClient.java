package com.banquito.Documentacion.client;

import com.banquito.Documentacion.util.dto.SolicitudResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "solicitudResumenFeignClient", url = "http://18.216.48.91:8081")
public interface VehiculoFeignClient {
    @GetMapping("/api/v1/solicitudes/{id}/resumen")
    SolicitudResumenDTO obtenerResumenSolicitud(@PathVariable("id") Long id);
}
