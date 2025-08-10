package com.banquito.Documentacion.enums;

public enum PagareEstado {
    PENDIENTE("pendiente"),
    PAGADO("pagado"),
    VENCIDO("vencido");

    private final String valor;

    PagareEstado(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
} 