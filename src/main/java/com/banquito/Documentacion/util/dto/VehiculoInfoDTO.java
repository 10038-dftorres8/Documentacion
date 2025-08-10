package com.banquito.Documentacion.util.dto;

import lombok.Data;

@Data
public class VehiculoInfoDTO {
    private String id;
    private String marca;
    private String modelo;
    private Double cilindraje;
    private String anio;
    private Double valor;
    private String color;
    private String extras;
    private String estado;
    private String tipo;
    private String combustible;
    private String condicion;
    private Integer version;
    private IdentificadorVehiculoDTO identificadorVehiculo;

    @Data
    public static class IdentificadorVehiculoDTO {
        private String placa;
        private String chasis;
        private String motor;
        private String id;
    }
}
