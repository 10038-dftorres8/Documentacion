package com.banquito.Documentacion.exception;

public class PagareGenerationException extends RuntimeException {

    public PagareGenerationException(String message) {
        super(message);
    }

    public PagareGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
