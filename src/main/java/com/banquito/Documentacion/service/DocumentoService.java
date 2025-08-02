package com.banquito.Documentacion.service;

import com.banquito.Documentacion.dto.DocumentoAdjuntoDTO;
import com.banquito.Documentacion.dto.DocumentoAdjuntoResponseDTO;
import com.banquito.Documentacion.enums.TipoDocumentoEnum;
import com.banquito.Documentacion.exception.CreateEntityException;
import com.banquito.Documentacion.exception.ResourceNotFoundException;
import com.banquito.Documentacion.mapper.DocumentoAdjuntoMapper;
import com.banquito.Documentacion.model.DocumentoAdjunto;
import com.banquito.Documentacion.repository.DocumentoAdjuntoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentoService {
    
    private final DocumentoAdjuntoRepository documentoAdjuntoRepository;
    private final DocumentoAdjuntoMapper documentoAdjuntoMapper;
    private final FileStorageService fileStorageService;

    public DocumentoAdjuntoResponseDTO cargarDocumento(String numeroSolicitud, MultipartFile archivo, String tipoDocumento) {
        // Validar que el tipo de documento es válido
        try {
            TipoDocumentoEnum.valueOf(tipoDocumento);
        } catch (IllegalArgumentException e) {
            throw new CreateEntityException("DocumentoAdjunto", "Tipo de documento no válido: " + tipoDocumento);
        }

        // Validar que no existe ya un documento de este tipo para esta solicitud
        if (documentoAdjuntoRepository.existsByNumeroSolicitudAndTipoDocumento(numeroSolicitud, tipoDocumento)) {
            throw new CreateEntityException("DocumentoAdjunto", "Ya existe un documento de tipo " + tipoDocumento + " para esta solicitud");
        }

        String rutaArchivo = fileStorageService.storeFile(archivo, numeroSolicitud, tipoDocumento);
        
        // Extraer el nombre del archivo de la ruta generada
        String nombreArchivo = rutaArchivo.substring(rutaArchivo.lastIndexOf("/") + 1);
        
        // Crear DTO y usar mapper para convertir a entidad
        DocumentoAdjuntoDTO documentoDTO = new DocumentoAdjuntoDTO(
            numeroSolicitud, tipoDocumento, nombreArchivo, 
            rutaArchivo, LocalDateTime.now(), 1L
        );
        
        DocumentoAdjunto doc = documentoAdjuntoMapper.toEntity(documentoDTO);
        DocumentoAdjunto saved = documentoAdjuntoRepository.save(doc);
        log.info("Documento cargado exitosamente: {} para solicitud {}", tipoDocumento, numeroSolicitud);
        return documentoAdjuntoMapper.toResponseDTO(saved);
    }

    public Resource descargarDocumento(String numeroSolicitud, String idDocumento) {
        DocumentoAdjunto documento = documentoAdjuntoRepository.findById(idDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
        
        if (!documento.getNumeroSolicitud().equals(numeroSolicitud)) {
            throw new CreateEntityException("DocumentoAdjunto", "El documento no pertenece a la solicitud especificada");
        }
        
        return fileStorageService.loadFileAsResource(documento.getRutaStorage());
    }

    public List<DocumentoAdjuntoResponseDTO> listarDocumentos(String numeroSolicitud) {
        List<DocumentoAdjunto> documentos = documentoAdjuntoRepository.findByNumeroSolicitud(numeroSolicitud);
        return documentos.stream()
                .map(documentoAdjuntoMapper::toResponseDTO)
                .toList();
    }

    public void eliminarDocumento(String numeroSolicitud, String idDocumento) {
        DocumentoAdjunto documento = documentoAdjuntoRepository.findById(idDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
        
        if (!documento.getNumeroSolicitud().equals(numeroSolicitud)) {
            throw new CreateEntityException("DocumentoAdjunto", "El documento no pertenece a la solicitud especificada");
        }
        
        // Eliminar archivo físico
        fileStorageService.deleteFile(documento.getRutaStorage());
        
        // Eliminar registro de la base de datos
        documentoAdjuntoRepository.delete(documento);
        log.info("Documento eliminado exitosamente: {} de solicitud {}", idDocumento, numeroSolicitud);
    }

    public void eliminarDocumentosPorSolicitud(String numeroSolicitud) {
        List<DocumentoAdjunto> documentos = documentoAdjuntoRepository.findByNumeroSolicitud(numeroSolicitud);
        
        // Eliminar archivos físicos
        documentos.forEach(doc -> fileStorageService.deleteFile(doc.getRutaStorage()));
        
        // Eliminar registros de la base de datos
        documentoAdjuntoRepository.deleteByNumeroSolicitud(numeroSolicitud);
        log.info("Documentos eliminados exitosamente para solicitud: {}", numeroSolicitud);
    }
} 