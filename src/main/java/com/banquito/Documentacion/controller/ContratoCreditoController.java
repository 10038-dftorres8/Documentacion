
package com.banquito.Documentacion.controller;

import com.banquito.Documentacion.dto.*;
import com.banquito.Documentacion.enums.ContratoCreditoEstado;
import com.banquito.Documentacion.service.ContratoCreditoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.banquito.Documentacion.client.PersonaFeignClient;
import com.banquito.Documentacion.client.VehiculoFeignClient;
import com.banquito.Documentacion.client.SolicitudCreditoClient;
import com.banquito.Documentacion.config.BancoProperties;
import com.banquito.Documentacion.util.ContratoCreditoPdfUtil;
import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
import com.banquito.Documentacion.util.dto.VehiculoInfoDTO;
import com.banquito.Documentacion.util.dto.SolicitudResumenDTO;
import com.banquito.Documentacion.util.dto.SolicitudCompletaDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping(path = "/api/contratos-credito", produces = "application/json")
@Tag(name = "Contratos de Crédito", description = "API para gestionar Contratos de Crédito Automotriz y sus Pagarés")
@Validated
public class ContratoCreditoController {

    private static final Logger log = LoggerFactory.getLogger(ContratoCreditoController.class);
    private final ContratoCreditoService service;
    private final PersonaFeignClient personaFeignClient;
    private final VehiculoFeignClient vehiculoFeignClient;
    private final SolicitudCreditoClient solicitudCreditoClient;
    private final BancoProperties bancoProperties;

    public ContratoCreditoController(ContratoCreditoService service, PersonaFeignClient personaFeignClient, VehiculoFeignClient vehiculoFeignClient, SolicitudCreditoClient solicitudCreditoClient, BancoProperties bancoProperties) {
        this.service = service;
        this.personaFeignClient = personaFeignClient;
        this.vehiculoFeignClient = vehiculoFeignClient;
        this.solicitudCreditoClient = solicitudCreditoClient;
        this.bancoProperties = bancoProperties;
    }
    @Operation(summary = "Genera y descarga el PDF del contrato de crédito")
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> descargarPdfContrato(@PathVariable Long id) {
        ContratoCreditoDTO contrato = service.getContratoCreditoById(id);
        
        // Obtener datos reales de identificación desde la solicitud
        String tipoIdentificacion = null;
        String numeroIdentificacion = null;
        
        try {
            SolicitudCompletaDTO solicitudCompleta = solicitudCreditoClient.obtenerSolicitudCompleta(contrato.getIdSolicitud());
            if (solicitudCompleta != null) {
                tipoIdentificacion = solicitudCompleta.getTipoIdentificacionCliente();
                numeroIdentificacion = solicitudCompleta.getNumeroIdentificacionCliente();
                log.info("Datos de identificación obtenidos desde solicitud: tipo={}, numero={}", tipoIdentificacion, numeroIdentificacion);
            }
        } catch (Exception ex) {
            log.warn("No se pudo obtener la solicitud completa para idSolicitud={}: {}", contrato.getIdSolicitud(), ex.getMessage());
        }
        
        // Si no se pudieron obtener los datos, usar valores por defecto para testing
        if (tipoIdentificacion == null || numeroIdentificacion == null) {
            tipoIdentificacion = "CEDULA";
            numeroIdentificacion = "0102030405";
            log.warn("Usando datos de identificación por defecto para testing: tipo={}, numero={}", tipoIdentificacion, numeroIdentificacion);
        }

        PersonaInfoDTO persona = null;
        try {
            persona = personaFeignClient.obtenerPersonaPorIdentificacion(tipoIdentificacion, numeroIdentificacion);
            if (persona == null) {
                log.warn("No se pudo obtener datos de persona para tipo={} numero={}", tipoIdentificacion, numeroIdentificacion);
                persona = new PersonaInfoDTO();
                // Establecer datos por defecto si no se encuentra la persona
                persona.setTipoIdentificacion(tipoIdentificacion);
                persona.setNumeroIdentificacion(numeroIdentificacion);
                persona.setNombre("Cliente No Encontrado");
            } else {
                log.info("Datos de persona obtenidos exitosamente para: {}", persona.getNombre());
            }
        } catch (Exception ex) {
            log.warn("Error al conectar con el servicio de persona (tipo={}, numero={}): {}", tipoIdentificacion, numeroIdentificacion, ex.getMessage());
            persona = new PersonaInfoDTO();
            persona.setTipoIdentificacion(tipoIdentificacion);
            persona.setNumeroIdentificacion(numeroIdentificacion);
            persona.setNombre("Error al obtener datos");
        }

        VehiculoInfoDTO vehiculo = null;
        try {
            SolicitudResumenDTO resumen = vehiculoFeignClient.obtenerResumenSolicitud(contrato.getIdSolicitud());
            if (resumen == null) {
                log.warn("No se pudo obtener resumen de solicitud para idSolicitud={}", contrato.getIdSolicitud());
                vehiculo = new VehiculoInfoDTO();
            } else {
                vehiculo = new VehiculoInfoDTO();
                vehiculo.setMarca(resumen.getMarcaVehiculo());
                vehiculo.setModelo(resumen.getModeloVehiculo());
                vehiculo.setAnio(null); // No viene en el resumen
                vehiculo.setValor(resumen.getValorVehiculo() != null ? resumen.getValorVehiculo().doubleValue() : null);
                vehiculo.setColor(null); // No viene en el resumen
                vehiculo.setExtras(null); // No viene en el resumen
                vehiculo.setEstado(null); // No viene en el resumen
                vehiculo.setTipo(null); // No viene en el resumen
                vehiculo.setCombustible(null); // No viene en el resumen
                vehiculo.setCondicion(null); // No viene en el resumen
                vehiculo.setIdentificadorVehiculo(null); // No viene en el resumen
            }
        } catch (Exception ex) {
            log.warn("No se pudo conectar al servicio externo de solicitud/resumen: {}", ex.getMessage());
            vehiculo = new VehiculoInfoDTO();
        }

        try {
            byte[] pdfBytes = ContratoCreditoPdfUtil.generarPdfContrato(
                contrato,
                persona,
                vehiculo,
                bancoProperties.getNombre(),
                bancoProperties.getDireccion(),
                bancoProperties.getTelefono(),
                bancoProperties.getEmail(),
                bancoProperties.getRepresentante(),
                bancoProperties.getCedulaRepresentante()
            );
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return ResponseEntity.ok()
                .contentLength(pdfBytes.length)
                .header("Content-Disposition", "attachment; filename=contrato-credito-" + contrato.getNumeroContrato() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } catch (Exception e) {
            log.error("Error generando PDF del contrato", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === CONTRATO CREDITO ===

    @Operation(summary = "Obtiene un Contrato de Crédito por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<ContratoCreditoDTO> getById(
        @Parameter(description = "ID del contrato", required = true)
        @PathVariable Long id) {
        log.debug("Solicitud recibida → Obtener ContratoCredito con ID={}", id);
        ContratoCreditoDTO dto = service.getContratoCreditoById(id);
        log.info("ContratoCredito ID={} recuperado correctamente.", id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Crea un nuevo Contrato de Crédito")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<ContratoCreditoDTO> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload para crear el Contrato de Crédito",
            required = true,
            content = @Content(schema = @Schema(implementation = ContratoCreditoCreateDTO.class))
        )
        @Valid @RequestBody ContratoCreditoCreateDTO createDto) {
        log.debug("Solicitud recibida → Crear ContratoCredito para solicitud={}", createDto.getIdSolicitud());
        ContratoCreditoDTO created = service.createContratoCredito(createDto);
        log.info("ContratoCredito creado correctamente con ID={}", created.getIdContratoCredito());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualiza un Contrato de Crédito existente")
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<ContratoCreditoDTO> update(
        @Parameter(description = "ID del contrato a actualizar", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload para actualizar el Contrato de Crédito",
            required = true,
            content = @Content(schema = @Schema(implementation = ContratoCreditoUpdateDTO.class))
        )
        @Valid @RequestBody ContratoCreditoUpdateDTO updateDto) {
        log.debug("Solicitud recibida → Actualizar ContratoCredito ID={}", id);
        ContratoCreditoDTO updated = service.updateContratoCredito(id, updateDto);
        log.info("ContratoCredito ID={} actualizado correctamente.", id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Elimina lógicamente un Contrato de Crédito (marca como CANCELADO)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ContratoCreditoDTO> logicalDelete(
        @Parameter(description = "ID del contrato a eliminar", required = true)
        @PathVariable Long id) {
        log.debug("Solicitud recibida → Eliminación lógica de ContratoCredito ID={}", id);
        ContratoCreditoDTO deleted = service.logicalDeleteContratoCredito(id);
        log.warn("ContratoCredito ID={} marcado como CANCELADO.", id);
        return ResponseEntity.ok(deleted);
    }

    @Operation(summary = "Lista contratos con filtros y paginación")
    @GetMapping
    public ResponseEntity<Page<ContratoCreditoDTO>> listWithFilters(
        @Parameter(description = "Estado del contrato") @RequestParam(required = false) ContratoCreditoEstado estado,
        @Parameter(description = "Número de contrato core (búsqueda parcial)") @RequestParam(required = false) String numeroContrato,
        @Parameter(description = "ID de solicitud") @RequestParam(required = false) Long idSolicitud,
        @Parameter(description = "Página", example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ContratoCreditoDTO> result = service.findContratosConFiltros(estado, numeroContrato, idSolicitud, pageable);
        log.info("Consulta contratos: encontrados {} resultados.", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Verifica si existe un contrato para una solicitud")
    @GetMapping("/existe/solicitud/{idSolicitud}")
    public ResponseEntity<Boolean> existsBySolicitud(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud) {
        log.debug("Verificando existencia de contrato para solicitud {}", idSolicitud);
        boolean existe = service.existePorSolicitud(idSolicitud);
        log.info("Existencia de contrato para solicitud {}: {}", idSolicitud, existe);
        return ResponseEntity.ok(existe);
    }

    // === PAGARE (Integrados) ===

    @GetMapping("/pagares/{id}")
    @Operation(summary = "Obtener pagaré por ID", description = "Obtiene un pagaré específico por su ID")
    public ResponseEntity<PagareDTO> getPagareById(@PathVariable Long id) {
        log.info("Solicitando pagaré por ID: {}", id);
        PagareDTO pagare = service.getPagareById(id);
        return ResponseEntity.ok(pagare);
    }

    @GetMapping("/pagares/contrato/{idContratoCredito}")
    @Operation(summary = "Obtener todos los pagarés de un contrato", description = "Obtiene la lista ordenada de pagarés de un contrato de crédito")
    public ResponseEntity<List<PagareDTO>> getPagaresByContrato(
            @PathVariable Long idContratoCredito) {
        log.info("Listando pagarés de contrato de crédito ID: {}", idContratoCredito);
        List<PagareDTO> pagares = service.getPagaresByContratoCredito(idContratoCredito);
        return ResponseEntity.ok(pagares);
    }

    @GetMapping("/pagares/contrato/{idContratoCredito}/cuota/{numeroCuota}")
    @Operation(summary = "Obtener un pagaré de un contrato por número de cuota", description = "Obtiene el pagaré de un contrato para una cuota específica")
    public ResponseEntity<PagareDTO> getPagareByContratoAndCuota(
            @PathVariable Long idContratoCredito,
            @PathVariable Long numeroCuota) {
        log.info("Buscando pagaré por contrato {} y cuota {}", idContratoCredito, numeroCuota);
        PagareDTO pagare = service.getPagareByContratoAndCuota(idContratoCredito, numeroCuota);
        return ResponseEntity.ok(pagare);
    }

    @PostMapping("/pagares")
    @Operation(summary = "Crear un nuevo pagaré manual", description = "Permite crear un pagaré de forma manual (casos excepcionales)")
    public ResponseEntity<PagareDTO> createPagare(@Valid @RequestBody PagareCreateDTO dto) {
        log.info("Creando pagaré manual para contrato {}", dto.getIdContratoCredito());
        PagareDTO pagare = service.createPagare(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagare);
    }

    @PutMapping("/pagares/{id}")
    @Operation(summary = "Actualizar pagaré", description = "Actualiza los datos de un pagaré existente")
    public ResponseEntity<PagareDTO> updatePagare(
            @PathVariable Long id,
            @Valid @RequestBody PagareUpdateDTO dto) {
        log.info("Actualizando pagaré ID: {}", id);
        PagareDTO pagareActualizado = service.updatePagare(id, dto);
        return ResponseEntity.ok(pagareActualizado);
    }

    @PostMapping("/pagares/generar")
    @Operation(summary = "Generar cronograma completo de pagarés", description = "Genera N pagarés automáticos para un contrato, uno por cada mes")
    public ResponseEntity<List<PagareDTO>> generarPagares(
            @Parameter(description = "ID del contrato de crédito") @RequestParam Long idContratoCredito) {
        List<PagareDTO> pagares = service.generarPagaresDesdeContrato(idContratoCredito);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagares);
    }


        @GetMapping("/pagares/contrato/{idContratoCredito}/existen")
@Operation(summary = "Verificar si existen pagarés para un contrato")
public ResponseEntity<Boolean> existenPagaresPorContrato(@PathVariable Long idContratoCredito) {
    boolean existen = service.existenPagaresPorContrato(idContratoCredito);
    return ResponseEntity.ok(existen);
}

    @GetMapping(value = "/{idContratoCredito}/pagares/{numeroCuota}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Genera y descarga el PDF del pagaré por contrato y cuota")
    public ResponseEntity<ByteArrayResource> descargarPdfPagarePorContrato(
            @PathVariable Long idContratoCredito, 
            @PathVariable Long numeroCuota) {
        
        log.debug("Generando PDF del pagaré para contrato {} cuota {}", idContratoCredito, numeroCuota);
        
        // Obtener el pagaré por contrato y cuota
        PagareDTO pagare = service.getPagareByContratoAndCuota(idContratoCredito, numeroCuota);
        ContratoCreditoDTO contrato = service.getContratoCreditoById(idContratoCredito);

        // Intentar obtener datos reales del deudor desde la solicitud asociada
        String tipoIdentificacion = null;
        String numeroIdentificacion = null;
        String nombreCliente = null;
        String correoCliente = null;
        
        try {
            if (contrato.getIdSolicitud() != null) {
                log.debug("Obteniendo datos de solicitud para idSolicitud={}", contrato.getIdSolicitud());
                
                // Intentar obtener la solicitud completa con datos del cliente
                com.banquito.Documentacion.util.dto.SolicitudCompletaDTO solicitudCompleta = null;
                try {
                    solicitudCompleta = solicitudCreditoClient.obtenerSolicitudCompleta(contrato.getIdSolicitud());
                    log.debug("Solicitud completa obtenida exitosamente");
                } catch (Exception ex) {
                    log.warn("No se pudo obtener solicitud completa para idSolicitud={}: {}", contrato.getIdSolicitud(), ex.getMessage());
                }
                
                if (solicitudCompleta != null) {
                    // Usar datos del cliente de la solicitud completa
                    tipoIdentificacion = solicitudCompleta.getTipoIdentificacionCliente();
                    numeroIdentificacion = solicitudCompleta.getNumeroIdentificacionCliente();
                    nombreCliente = solicitudCompleta.getNombreCliente();
                    correoCliente = solicitudCompleta.getCorreoCliente();
                    log.debug("Datos del cliente obtenidos de solicitud completa: {} - {}", tipoIdentificacion, numeroIdentificacion);
                } else {
                    // Fallback: usar resumen de solicitud si no se puede obtener la completa
                    log.debug("Intentando obtener resumen de solicitud como fallback");
                    com.banquito.Documentacion.util.dto.SolicitudResumenDTO resumen = null;
                    try {
                        resumen = vehiculoFeignClient.obtenerResumenSolicitud(contrato.getIdSolicitud());
                    } catch (Exception ex) {
                        log.warn("No se pudo obtener resumen de solicitud para idSolicitud={}: {}", contrato.getIdSolicitud(), ex.getMessage());
                    }
                    if (resumen != null) {
                        tipoIdentificacion = "CEDULA"; // Valor por defecto si no está disponible
                        numeroIdentificacion = resumen.getCedulaCliente();
                        nombreCliente = resumen.getNombresCliente();
                        correoCliente = resumen.getCorreoCliente();
                        log.debug("Datos del cliente obtenidos de resumen de solicitud: {}", numeroIdentificacion);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Error al obtener datos de solicitud para el pagaré: {}", ex.getMessage());
        }

        // Validar que se obtuvieron datos mínimos necesarios
        if (tipoIdentificacion == null || numeroIdentificacion == null) {
            log.warn("No se pudieron obtener datos de identificación del cliente para el pagaré. Usando valores por defecto.");
            tipoIdentificacion = "CEDULA";
            numeroIdentificacion = "0000000000"; // Número genérico que indica error en obtención de datos
            nombreCliente = "CLIENTE NO IDENTIFICADO";
        }

        PersonaInfoDTO persona = null;
        boolean datosPersonaFeign = false;
        
        // Intentar obtener datos adicionales del servicio de personas
        try {
            log.debug("Intentando obtener datos de persona desde servicio externo: {} - {}", tipoIdentificacion, numeroIdentificacion);
            persona = personaFeignClient.obtenerPersonaPorIdentificacion(tipoIdentificacion, numeroIdentificacion);
            if (persona != null && persona.getNombre() != null && !persona.getNombre().isEmpty()) {
                datosPersonaFeign = true;
                log.info("Datos de persona obtenidos exitosamente desde servicio externo: {}", persona.getNombre());
            }
        } catch (Exception ex) {
            log.warn("Error al obtener datos de persona desde servicio externo: {}", ex.getMessage());
        }

        // Si no se pudieron obtener datos del servicio de personas, crear persona con datos disponibles
        if (persona == null || !datosPersonaFeign) {
            log.debug("Creando PersonaInfoDTO con datos disponibles de la solicitud");
            persona = new PersonaInfoDTO();
            persona.setNombre(nombreCliente != null ? nombreCliente : "CLIENTE NO IDENTIFICADO");
            persona.setTipoIdentificacion(tipoIdentificacion);
            persona.setNumeroIdentificacion(numeroIdentificacion);
            persona.setCorreoElectronico(correoCliente);
            // Campos adicionales con valores por defecto
            persona.setGenero("NO_ESPECIFICADO");
            persona.setFechaNacimiento("N/A");
            persona.setEstadoCivil("NO_ESPECIFICADO");
            persona.setNivelEstudio("NO_ESPECIFICADO");
        }

        try {
            log.debug("Generando PDF del pagaré para la cuota {}", pagare.getNumeroCuota());
            byte[] pdfBytes = com.banquito.Documentacion.util.PagareCreditoPdfUtil.generarPdfPagare(
                pagare,
                persona,
                bancoProperties.getNombre(),
                bancoProperties.getDireccion(),
                bancoProperties.getTelefono(),
                bancoProperties.getEmail(),
                bancoProperties.getRepresentante(),
                bancoProperties.getCedulaRepresentante()
            );
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            log.info("PDF del pagaré generado exitosamente para cuota {}, cliente: {}", pagare.getNumeroCuota(), persona.getNombre());
            return ResponseEntity.ok()
                .contentLength(pdfBytes.length)
                .header("Content-Disposition", "attachment; filename=pagare_cuota_" + pagare.getNumeroCuota() + ".pdf")
                .body(resource);
        } catch (Exception e) {
            log.error("Error generando PDF del pagaré: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ByteArrayResource("Error al generar PDF del pagaré".getBytes()));
        }
    }

    @GetMapping(value = "/{idContratoCredito}/pagares/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Genera y descarga el PDF del primer pagaré de un contrato")
    public ResponseEntity<ByteArrayResource> descargarPdfPrimerPagare(@PathVariable Long idContratoCredito) {
        log.debug("Generando PDF del primer pagaré para contrato {}", idContratoCredito);
        
        // Obtener el primer pagaré del contrato (cuota 1)
        return descargarPdfPagarePorContrato(idContratoCredito, 1L);
    }

    @GetMapping(value = "/pagares/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Genera y descarga el PDF del pagaré por ID")
    public ResponseEntity<ByteArrayResource> descargarPdfPagare(@PathVariable Long id) {
        PagareDTO pagare = service.getPagareById(id);
        ContratoCreditoDTO contrato = service.getContratoCreditoById(pagare.getIdContratoCredito());

        // Intentar obtener datos reales del deudor desde la solicitud asociada
        String tipoIdentificacion = null;
        String numeroIdentificacion = null;
        String nombreCliente = null;
        String correoCliente = null;
        
        try {
            if (contrato.getIdSolicitud() != null) {
                log.debug("Obteniendo datos de solicitud para idSolicitud={}", contrato.getIdSolicitud());
                
                // Intentar obtener la solicitud completa con datos del cliente
                com.banquito.Documentacion.util.dto.SolicitudCompletaDTO solicitudCompleta = null;
                try {
                    solicitudCompleta = solicitudCreditoClient.obtenerSolicitudCompleta(contrato.getIdSolicitud());
                    log.debug("Solicitud completa obtenida exitosamente");
                } catch (Exception ex) {
                    log.warn("No se pudo obtener solicitud completa para idSolicitud={}: {}", contrato.getIdSolicitud(), ex.getMessage());
                }
                
                if (solicitudCompleta != null) {
                    // Usar datos del cliente de la solicitud completa
                    tipoIdentificacion = solicitudCompleta.getTipoIdentificacionCliente();
                    numeroIdentificacion = solicitudCompleta.getNumeroIdentificacionCliente();
                    nombreCliente = solicitudCompleta.getNombreCliente();
                    correoCliente = solicitudCompleta.getCorreoCliente();
                    log.debug("Datos del cliente obtenidos de solicitud completa: {} - {}", tipoIdentificacion, numeroIdentificacion);
                } else {
                    // Fallback: usar resumen de solicitud si no se puede obtener la completa
                    log.debug("Intentando obtener resumen de solicitud como fallback");
                    com.banquito.Documentacion.util.dto.SolicitudResumenDTO resumen = null;
                    try {
                        resumen = vehiculoFeignClient.obtenerResumenSolicitud(contrato.getIdSolicitud());
                    } catch (Exception ex) {
                        log.warn("No se pudo obtener resumen de solicitud para idSolicitud={}: {}", contrato.getIdSolicitud(), ex.getMessage());
                    }
                    if (resumen != null) {
                        tipoIdentificacion = "CEDULA"; // Valor por defecto si no está disponible
                        numeroIdentificacion = resumen.getCedulaCliente();
                        nombreCliente = resumen.getNombresCliente();
                        correoCliente = resumen.getCorreoCliente();
                        log.debug("Datos del cliente obtenidos de resumen de solicitud: {}", numeroIdentificacion);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Error al obtener datos de solicitud para el contrato: {}", ex.getMessage());
        }

        // Validar que se obtuvieron datos mínimos necesarios
        if (tipoIdentificacion == null || numeroIdentificacion == null) {
            log.warn("No se pudieron obtener datos de identificación del cliente para el pagaré. Usando valores por defecto.");
            tipoIdentificacion = "CEDULA";
            numeroIdentificacion = "0000000000"; // Número genérico que indica error en obtención de datos
            nombreCliente = "CLIENTE NO IDENTIFICADO";
        }

        PersonaInfoDTO persona = null;
        boolean datosPersonaFeign = false;
        
        // Intentar obtener datos adicionales del servicio de personas
        try {
            log.debug("Intentando obtener datos de persona desde servicio externo: {} - {}", tipoIdentificacion, numeroIdentificacion);
            persona = personaFeignClient.obtenerPersonaPorIdentificacion(tipoIdentificacion, numeroIdentificacion);
            if (persona != null && persona.getNombre() != null && !persona.getNombre().isEmpty()) {
                log.debug("Datos de persona obtenidos exitosamente desde servicio externo: {}", persona.getNombre());
                datosPersonaFeign = true;
            } else {
                log.debug("Servicio de personas retornó datos vacíos o nulos");
            }
        } catch (Exception ex) {
            log.warn("No se pudo conectar al servicio externo de persona: {}", ex.getMessage());
        }
        
        // Si no se pudo obtener por Feign, usar datos de la solicitud
        if (!datosPersonaFeign) {
            log.debug("Usando datos de solicitud para crear PersonaInfoDTO");
            persona = new PersonaInfoDTO();
            persona.setTipoIdentificacion(tipoIdentificacion);
            persona.setNumeroIdentificacion(numeroIdentificacion);
            persona.setNombre(nombreCliente != null ? nombreCliente : "N/A");
            persona.setCorreoElectronico(correoCliente);
        }

        try {
            log.debug("Generando PDF del pagaré para la cuota {}", pagare.getNumeroCuota());
            byte[] pdfBytes = com.banquito.Documentacion.util.PagareCreditoPdfUtil.generarPdfPagare(
                pagare,
                persona,
                bancoProperties.getNombre(),
                bancoProperties.getDireccion(),
                bancoProperties.getTelefono(),
                bancoProperties.getEmail(),
                bancoProperties.getRepresentante(),
                bancoProperties.getCedulaRepresentante()
            );
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            log.info("PDF del pagaré generado exitosamente para cuota {}, cliente: {}", pagare.getNumeroCuota(), persona.getNombre());
            return ResponseEntity.ok()
                .contentLength(pdfBytes.length)
                .header("Content-Disposition", "attachment; filename=pagare-cuota-" + pagare.getNumeroCuota() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } catch (Exception e) {
            log.error("Error generando PDF del pagaré: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ByteArrayResource("Error al generar PDF del pagaré".getBytes()));
        }
    }
}
