package com.banquito.Documentacion.service;

import com.banquito.Documentacion.dto.CrearPrestamoQueueDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrestamoQueueService {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    private static final String QUEUE_NAME = "solicitud.prestamos";

    public void enviarSolicitudPrestamo(CrearPrestamoQueueDTO prestamoDTO) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(prestamoDTO);
            
            jmsTemplate.convertAndSend(QUEUE_NAME, mensajeJson);
            
            log.info("Mensaje enviado a la cola {}: {}", QUEUE_NAME, mensajeJson);
            
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el mensaje para la cola: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar mensaje a la cola", e);
        } catch (Exception e) {
            log.error("Error al enviar mensaje a la cola {}: {}", QUEUE_NAME, e.getMessage(), e);
            throw new RuntimeException("Error al enviar mensaje a la cola", e);
        }
    }
}
