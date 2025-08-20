package com.banquito.Documentacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.banquito.Documentacion.dto.DetalleSolicitudResponseDTO;

@FeignClient(name = "originacionClient", url = "${originacion.service.url}")
public interface OriginacionClient {
    /**
     * GET /v1/solicitudes/{numeroSolicitud}/detalle
     * numeroSolicitud es un String tipo SOL-20250806-7451
     */
    @GetMapping("/v1/solicitudes/{numeroSolicitud}/detalle")
    DetalleSolicitudResponseDTO obtenerDetalle(@PathVariable("numeroSolicitud") String numeroSolicitud);

    /**
     * POST /v1/solicitudes/{idSolicitud}/cambiar-estado
     */
    @PostMapping("/v1/solicitudes/{idSolicitud}/cambiar-estado")
    void cambiarEstado(
        @PathVariable("idSolicitud") Long idSolicitud,
        @RequestParam("nuevoEstado") String nuevoEstado,
        @RequestParam("motivo") String motivo,
        @RequestParam("usuario") String usuario
    );
}

