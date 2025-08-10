package com.banquito.Documentacion.client;

import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
import com.banquito.Documentacion.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(
    name = "persona-service", 
    url = "${persona.service.url:http://3.145.79.87:8083}",
    configuration = FeignConfig.class
)
public interface PersonaFeignClient {
    
    @GetMapping("/api/v1/clientes/personas/{tipoIdentificacion}/{numeroIdentificacion}")
    PersonaInfoDTO obtenerPersonaPorIdentificacion(
        @PathVariable("tipoIdentificacion") String tipoIdentificacion, 
        @PathVariable("numeroIdentificacion") String numeroIdentificacion
    );
    
    @GetMapping("/api/v1/clientes/personas")
    List<PersonaInfoDTO> obtenerTodasLasPersonas();
}
