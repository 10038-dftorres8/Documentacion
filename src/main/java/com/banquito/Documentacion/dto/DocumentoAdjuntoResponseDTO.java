package com.banquito.Documentacion.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.banquito.Documentacion.enums.EstadoDocumentoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Data
public class DocumentoAdjuntoResponseDTO {
    private String id;
    private String numeroSolicitud;
    private String tipoDocumento;
    private String nombreArchivo;
    private String rutaStorage;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCarga;
    private EstadoDocumentoEnum estado;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;
    private Long version;
    private String observacion;
}