
package com.banquito.Documentacion.controller;
// import com.banquito.Documentacion.client.SolicitudCreditoClient; // Eliminado, no se usa
// import com.banquito.Documentacion.dto.SolicitudResumenDTO; // Eliminado, no se usa

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

import com.banquito.Documentacion.client.ClientesClient;
import com.banquito.Documentacion.client.OriginacionClient;
import com.banquito.Documentacion.config.BancoProperties;
// import com.banquito.Documentacion.util.ContratoCreditoPdfUtil; // Eliminado, no se usa
import com.banquito.Documentacion.util.dto.PersonaInfoDTO;
// import com.banquito.Documentacion.util.dto.VehiculoInfoDTO; // Eliminado, no se usa
// import com.banquito.Documentacion.util.dto.SolicitudResumenDTO; // Removed duplicate import
// import com.banquito.Documentacion.dto.SolicitudResumenDTO; // Eliminado, no se usa
// import com.banquito.Documentacion.util.dto.SolicitudCompletaDTO; // Eliminado, no se usa
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
    private final ClientesClient clientesClient;
    private final OriginacionClient originacionClient;
    private final BancoProperties bancoProperties;
    // private final SolicitudCreditoClient solicitudCreditoClient; // Eliminado, no se usa

    public ContratoCreditoController(ContratoCreditoService service, ClientesClient clientesClient, OriginacionClient originacionClient, BancoProperties bancoProperties) {
        this.service = service;
        this.clientesClient = clientesClient;
        this.originacionClient = originacionClient;
        this.bancoProperties = bancoProperties;
    }
    @Operation(summary = "Genera y descarga el PDF del contrato de crédito")
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> descargarPdfContrato(@PathVariable Long id) {
        ContratoCreditoDTO contrato = service.getContratoCreditoById(id);
        try {
                byte[] pdfBytes = service.generarPdfContrato(id, contrato);
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
        @Parameter(description = "Número de solicitud") @RequestParam(required = false) String numeroSolicitud,
        @Parameter(description = "Página", example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ContratoCreditoDTO> result = service.findContratosConFiltros(estado, numeroContrato, numeroSolicitud, pageable);
        log.info("Consulta contratos: encontrados {} resultados.", result.getTotalElements());
        return ResponseEntity.ok(result);
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

        // Obtener datos del cliente usando OriginacionClient y ClientesClient
        String tipoIdentificacion = null;
        String numeroIdentificacion = null;
        String nombreCliente = null;
        String correoCliente = null;

        try {
            var detalle = originacionClient.obtenerDetalle(contrato.getNumeroSolicitud());
            if (detalle != null) {
                tipoIdentificacion = "CEDULA";
                numeroIdentificacion = detalle.getCedulaSolicitante();
                // nombreCliente y correoCliente se pueden obtener de ClientesClient
            }
        } catch (Exception ex) {
            log.warn("No se pudo obtener el detalle de la solicitud para numeroSolicitud={}: {}", contrato.getNumeroSolicitud(), ex.getMessage());
        }

        if (tipoIdentificacion == null || numeroIdentificacion == null) {
            tipoIdentificacion = "CEDULA";
            numeroIdentificacion = "0000000000";
            nombreCliente = "CLIENTE NO IDENTIFICADO";
        }

        PersonaInfoDTO persona = new PersonaInfoDTO();
        try {
            var personaResp = clientesClient.consultarPersonaPorIdentificacion(tipoIdentificacion, numeroIdentificacion);
            if (personaResp != null) {
                persona.setTipoIdentificacion(personaResp.getTipoIdentificacion());
                persona.setNumeroIdentificacion(personaResp.getNumeroIdentificacion());
                persona.setNombre(personaResp.getNombres());
                persona.setCorreoElectronico(personaResp.getCorreoElectronico());
                // Completa otros campos si es necesario
            } else {
                persona.setTipoIdentificacion(tipoIdentificacion);
                persona.setNumeroIdentificacion(numeroIdentificacion);
                persona.setNombre(nombreCliente != null ? nombreCliente : "CLIENTE NO IDENTIFICADO");
                persona.setCorreoElectronico(correoCliente);
            }
        } catch (Exception ex) {
            log.warn("Error al conectar con el servicio de persona (tipo={}, numero={}): {}", tipoIdentificacion, numeroIdentificacion, ex.getMessage());
            persona.setTipoIdentificacion(tipoIdentificacion);
            persona.setNumeroIdentificacion(numeroIdentificacion);
            persona.setNombre(nombreCliente != null ? nombreCliente : "Error al obtener datos");
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

        // Obtener datos del cliente usando OriginacionClient y ClientesClient
        String tipoIdentificacion = null;
        String numeroIdentificacion = null;
        String nombreCliente = null;
        String correoCliente = null;

        try {
            var detalle = originacionClient.obtenerDetalle(contrato.getNumeroSolicitud());
            if (detalle != null) {
                tipoIdentificacion = "CEDULA";
                numeroIdentificacion = detalle.getCedulaSolicitante();
            }
        } catch (Exception ex) {
            log.warn("No se pudo obtener el detalle de la solicitud para numeroSolicitud={}: {}", contrato.getNumeroSolicitud(), ex.getMessage());
        }

        if (tipoIdentificacion == null || numeroIdentificacion == null) {
            tipoIdentificacion = "CEDULA";
            numeroIdentificacion = "0000000000";
            nombreCliente = "CLIENTE NO IDENTIFICADO";
        }

        PersonaInfoDTO persona = new PersonaInfoDTO();
        try {
            var personaResp = clientesClient.consultarPersonaPorIdentificacion(tipoIdentificacion, numeroIdentificacion);
            if (personaResp != null) {
                persona.setTipoIdentificacion(personaResp.getTipoIdentificacion());
                persona.setNumeroIdentificacion(personaResp.getNumeroIdentificacion());
                persona.setNombre(personaResp.getNombres());
                persona.setCorreoElectronico(personaResp.getCorreoElectronico());
            } else {
                persona.setTipoIdentificacion(tipoIdentificacion);
                persona.setNumeroIdentificacion(numeroIdentificacion);
                persona.setNombre(nombreCliente != null ? nombreCliente : "N/A");
                persona.setCorreoElectronico(correoCliente);
            }
        } catch (Exception ex) {
            log.warn("Error al conectar con el servicio de persona (tipo={}, numero={}): {}", tipoIdentificacion, numeroIdentificacion, ex.getMessage());
            persona.setTipoIdentificacion(tipoIdentificacion);
            persona.setNumeroIdentificacion(numeroIdentificacion);
            persona.setNombre(nombreCliente != null ? nombreCliente : "Error al obtener datos");
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
