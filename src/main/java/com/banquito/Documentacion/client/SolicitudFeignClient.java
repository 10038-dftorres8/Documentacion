package com.banquito.Documentacion.client;

import com.banquito.Documentacion.util.dto.SolicitudInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "solicitud-service", url = "http://18.216.48.91:8080")
public interface SolicitudFeignClient {
    @GetMapping("/api/v1/credit-analysis/{idSolicitud}")
    SolicitudInfoDTO obtenerSolicitudPorId(@PathVariable("idSolicitud") Long idSolicitud);
}
