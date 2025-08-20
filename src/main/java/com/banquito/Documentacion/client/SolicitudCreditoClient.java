package com.banquito.Documentacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// DTO sencillo para ejemplo, puedes personalizarlo según la respuesta real
import com.banquito.Documentacion.dto.SolicitudResumenDTO;
import com.banquito.Documentacion.util.dto.SolicitudCompletaDTO;

@FeignClient(name = "originacion", url = "${originacion.url}")
public interface SolicitudCreditoClient {

    @GetMapping("/api/v1/solicitudes/{id}/resumen")
    SolicitudResumenDTO obtenerSolicitudPorId(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/solicitudes/{id}")
    SolicitudCompletaDTO obtenerSolicitudCompleta(@PathVariable("id") Long id);

}
