package com.banquito.Documentacion.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients(basePackages = "com.banquito.Documentacion.client")
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            30, TimeUnit.SECONDS, // connectTimeout
            60, TimeUnit.SECONDS, // readTimeout
            true // followRedirects
        );
    }
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
    
    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            switch (response.status()) {
                case 404:
                    return new RuntimeException("Recurso no encontrado - " + methodKey + " - Status: " + response.status());
                case 500:
                    return new RuntimeException("Error interno del servidor - " + methodKey + " - Status: " + response.status());
                default:
                    return new RuntimeException("Error desconocido - " + methodKey + " - Status: " + response.status());
            }
        }
    }
}
