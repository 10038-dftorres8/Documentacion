package com.banquito.Documentacion.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentoAdjuntoResponseDTO {
    private String id;
    private String numeroSolicitud;
    private String tipoDocumento;
    private String nombreArchivo;
    private String rutaStorage;
    private LocalDateTime fechaCarga;
    private LocalDateTime fechaActualizacion;
    private Long version;
} 