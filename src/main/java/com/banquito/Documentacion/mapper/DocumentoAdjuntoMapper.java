package com.banquito.Documentacion.mapper;

import com.banquito.Documentacion.dto.DocumentoAdjuntoDTO;
import com.banquito.Documentacion.dto.DocumentoAdjuntoResponseDTO;
import com.banquito.Documentacion.model.DocumentoAdjunto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DocumentoAdjuntoMapper {
    DocumentoAdjuntoResponseDTO toResponseDTO(DocumentoAdjunto entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    DocumentoAdjunto toEntity(DocumentoAdjuntoDTO dto);
} 