package com.banquito.Documentacion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "banco")
@Getter
@Setter
public class BancoProperties {
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String representante;
    private String cedulaRepresentante;
}
