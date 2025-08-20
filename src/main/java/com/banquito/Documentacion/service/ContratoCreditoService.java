package com.banquito.Documentacion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.Documentacion.client.SolicitudCreditoClient;
import com.banquito.Documentacion.client.OriginacionClient;
import com.banquito.Documentacion.dto.*;
import com.banquito.Documentacion.mapper.ContratoCreditoMapper;
import com.banquito.Documentacion.mapper.PagareMapper;
import com.banquito.Documentacion.enums.ContratoCreditoEstado;
import com.banquito.Documentacion.enums.PagareEstado;
import com.banquito.Documentacion.exception.ContratoCreditoGenerationException;
import com.banquito.Documentacion.exception.NumeroContratoYaExisteException;
import com.banquito.Documentacion.exception.PagareGenerationException;
import com.banquito.Documentacion.model.ContratoCredito;
import com.banquito.Documentacion.model.Pagare;
import com.banquito.Documentacion.repository.ContratoCreditoRepository;
import com.banquito.Documentacion.repository.PagareRepository;

@Service
public class ContratoCreditoService {

    private static final Logger log = LoggerFactory.getLogger(ContratoCreditoService.class);
    private static final String CONTRATO_NO_ENCONTRADO = "Contrato no encontrado: ";
    private final ContratoCreditoRepository contratoCreditoRepository;
    private final PagareRepository pagareRepository;
    private final ContratoCreditoMapper contratoCreditoMapper;
    private final PagareMapper pagareMapper;
    private final SolicitudCreditoClient solicitudCreditoClient;
    private final OriginacionClient originacionClient;

    public ContratoCreditoService(
        ContratoCreditoRepository contratoCreditoRepository,
        PagareRepository pagareRepository,
        ContratoCreditoMapper contratoCreditoMapper,
        PagareMapper pagareMapper,
        SolicitudCreditoClient solicitudCreditoClient,
        OriginacionClient originacionClient
    ) {
        this.contratoCreditoRepository = contratoCreditoRepository;
        this.pagareRepository = pagareRepository;
        this.contratoCreditoMapper = contratoCreditoMapper;
        this.pagareMapper = pagareMapper;
    this.solicitudCreditoClient = solicitudCreditoClient;
    this.originacionClient = originacionClient;
    }

    // -------- CONTRATO CREDITO --------

    @Transactional
    public ContratoCreditoDTO getContratoCreditoById(Long id) {
        ContratoCredito contrato = contratoCreditoRepository.findById(id)
            .orElseThrow(() -> new ContratoCreditoGenerationException(CONTRATO_NO_ENCONTRADO + id));
        return contratoCreditoMapper.toDto(contrato);
    }

    /**
     * Lógica para obtener los datos del cliente y generar el PDF del contrato
     * usando numeroSolicitud como clave y OriginacionClient para obtener los datos.
     */
    public byte[] generarPdfContrato(Long idContrato, ContratoCreditoDTO contratoDto) {
        try {
            String numeroSolicitud = contratoDto.getNumeroSolicitud();
            if (numeroSolicitud == null || numeroSolicitud.isEmpty()) {
                throw new IllegalStateException("El contrato no tiene numeroSolicitud asociado");
            }
            DetalleSolicitudResponseDTO detalle = originacionClient.obtenerDetalle(numeroSolicitud);
            if (detalle == null) {
                throw new IllegalStateException("No se encontró detalle de la solicitud para el número: " + numeroSolicitud);
            }
            // Usar directamente el DTO de la solicitud para el PDF
            return com.banquito.Documentacion.util.ContratoCreditoPdfUtil.generarPdfContratoSoloDatosJson(detalle);
        } catch (Exception ex) {
            log.error("Error generando PDF del contrato {}: {}", idContrato, ex.getMessage(), ex);
            throw new ContratoCreditoGenerationException("Error generando PDF del contrato: " + ex.getMessage());
        }
    }

    // Cambia la obtención de detalle de solicitud para usar numeroSolicitud (String)
    public DetalleSolicitudResponseDTO obtenerDetalleSolicitudPorNumero(String numeroSolicitud) {
        return originacionClient.obtenerDetalle(numeroSolicitud);
    }

    @Transactional
    public ContratoCreditoDTO createContratoCredito(ContratoCreditoCreateDTO dto) {
        // 1. Consumir el MS de originación para obtener la solicitud real
        SolicitudResumenDTO solicitud = null;
        try {
            solicitud = solicitudCreditoClient.obtenerSolicitudPorId(dto.getIdSolicitud());
        } catch (Exception ex) {
            log.warn("No se pudo obtener la solicitud desde el MS de originación para el id: {}. Se usará el DTO recibido. Error: {}", dto.getIdSolicitud(), ex.getMessage());
        }

        // Validación de unicidad
        if (contratoCreditoRepository.existsByNumeroContrato(dto.getNumeroContrato())) {
            throw new NumeroContratoYaExisteException(dto.getNumeroContrato(), "ContratoCredito");
        }

        // Construir la entidad, priorizando datos externos si existen
        ContratoCredito contrato = contratoCreditoMapper.toEntity(dto);
        // SIEMPRE asignar el numeroSolicitud del DTO recibido (para todos los casos)
        contrato.setNumeroSolicitud(dto.getNumeroSolicitud());
        if (solicitud != null) {
            // Si el MS responde y trae un número de solicitud, sobreescribe
            // (descomenta si tu DTO de resumen lo tiene: solicitud.getNumeroSolicitud())
            // if (solicitud.getNumeroSolicitud() != null) contrato.setNumeroSolicitud(solicitud.getNumeroSolicitud());
            if (solicitud.getMontoAprobado() != null) contrato.setMontoAprobado(solicitud.getMontoAprobado());
            if (solicitud.getPlazoFinalMeses() != null) contrato.setPlazoFinalMeses(solicitud.getPlazoFinalMeses().longValue());
            if (solicitud.getTasaEfectivaAnual() != null) contrato.setTasaEfectivaAnual(solicitud.getTasaEfectivaAnual());
        }
        contrato.setEstado(ContratoCreditoEstado.PENDIENTE_FIRMA);
        contrato.setVersion(1L);

        // Guardar y retornar el DTO
        ContratoCredito saved = contratoCreditoRepository.save(contrato);
        return contratoCreditoMapper.toDto(saved);
    }

    @Transactional
    public ContratoCreditoDTO logicalDeleteContratoCredito(Long id) {
        ContratoCredito existing = contratoCreditoRepository.findById(id)
            .orElseThrow(() -> new ContratoCreditoGenerationException(CONTRATO_NO_ENCONTRADO + id));

        if (ContratoCreditoEstado.ACTIVO.equals(existing.getEstado())) {
            throw new ContratoCreditoGenerationException("El contrato ya está cancelado: " + id);
        }
        existing.setEstado(ContratoCreditoEstado.ACTIVO);
        ContratoCredito saved = contratoCreditoRepository.save(existing);
        return contratoCreditoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ContratoCreditoDTO> findContratosConFiltros(
        ContratoCreditoEstado estado,
        String numeroContrato,
        String numeroSolicitud,
        Pageable pageable
    ) {
        Page<ContratoCredito> contratos;
        if (estado != null && numeroContrato != null && numeroSolicitud != null) {
            contratos = contratoCreditoRepository.findByEstadoAndNumeroContratoContainingIgnoreCaseAndNumeroSolicitud(
                estado, numeroContrato, numeroSolicitud, pageable);
        } else if (estado != null && numeroContrato != null) {
            contratos = contratoCreditoRepository.findByEstadoAndNumeroContratoContainingIgnoreCase(
                estado, numeroContrato, pageable);
        } else if (estado != null && numeroSolicitud != null) {
            contratos = contratoCreditoRepository.findByEstadoAndNumeroSolicitud(estado, numeroSolicitud, pageable);
        } else if (numeroContrato != null && numeroSolicitud != null) {
            contratos = contratoCreditoRepository.findByNumeroContratoContainingIgnoreCaseAndNumeroSolicitud(
                numeroContrato, numeroSolicitud, pageable);
        } else if (estado != null) {
            contratos = contratoCreditoRepository.findByEstado(estado, pageable);
        } else if (numeroContrato != null) {
            contratos = contratoCreditoRepository.findByNumeroContratoContainingIgnoreCase(numeroContrato, pageable);
        } else if (numeroSolicitud != null) {
            contratos = contratoCreditoRepository.findByNumeroSolicitud(numeroSolicitud, pageable);
        } else {
            contratos = contratoCreditoRepository.findAll(pageable);
        }
        return contratos.map(contratoCreditoMapper::toDto);
    }

    // Método eliminado: existePorSolicitud(Long idSolicitud)

    // -------- PAGARE (Integrado) --------

    @Transactional
    public PagareDTO getPagareById(Long id) {
        Pagare pagare = pagareRepository.findById(id)
            .orElseThrow(() -> new PagareGenerationException("Pagaré no encontrado: " + id));
        return pagareMapper.toDto(pagare);
    }

    @Transactional
    public PagareDTO createPagare(PagareCreateDTO dto) {
        Pagare pagare = pagareMapper.toEntity(dto);
        Pagare saved = pagareRepository.save(pagare);
        return pagareMapper.toDto(saved);
    }

    @Transactional
    public List<PagareDTO> getPagaresByContratoCredito(Long idContratoCredito) {
        var pagares = pagareRepository.findByIdContratoCreditoOrderByNumeroCuota(idContratoCredito);
        return pagareMapper.toDtoList(pagares);
    }

    @Transactional
    public PagareDTO getPagareByContratoAndCuota(Long idContratoCredito, Long numeroCuota) {
        try {
            return pagareRepository
                .findByIdContratoCreditoAndNumeroCuota(idContratoCredito, numeroCuota)
                .map(pagareMapper::toDto)
                .orElseThrow(() ->
                    new PagareGenerationException(
                        "No se encontró el pagaré para contrato "
                        + idContratoCredito + " y cuota " + numeroCuota
                    )
                );
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException ex) {
            // Si hay múltiples resultados (datos duplicados), tomar el primero
            List<Pagare> pagares = pagareRepository.findByIdContratoCreditoOrderByNumeroCuota(idContratoCredito);
            Optional<Pagare> pagareEncontrado = pagares.stream()
                .filter(p -> p.getNumeroCuota().equals(numeroCuota))
                .findFirst();
            
            if (pagareEncontrado.isPresent()) {
                log.warn("Se encontraron múltiples pagarés para contrato {} cuota {}. Usando el primero.", 
                         idContratoCredito, numeroCuota);
                return pagareMapper.toDto(pagareEncontrado.get());
            } else {
                throw new PagareGenerationException(
                    "No se encontró el pagaré para contrato " + idContratoCredito + " y cuota " + numeroCuota
                );
            }
        }
    }

    @Transactional
    public PagareDTO updatePagare(Long id, PagareUpdateDTO dto) {
        if (!id.equals(dto.getIdPagare())) {
            throw new PagareGenerationException("El ID del path no coincide con el del body");
        }
        Pagare existing = pagareRepository.findById(id)
            .orElseThrow(() -> new PagareGenerationException("Pagaré no encontrado: " + id));
        pagareMapper.updateEntity(existing, dto);
        Pagare updated = pagareRepository.save(existing);
        return pagareMapper.toDto(updated);
    }

    @Transactional
    public List<PagareDTO> generarPagaresDesdeParams(
        Long idContratoCredito,
        BigDecimal montoSolicitado,
        BigDecimal tasaAnual,
        int plazoMeses,
        LocalDate fechaInicio
    ) {
        if (pagareRepository.existsByIdContratoCredito(idContratoCredito)) {
            throw new PagareGenerationException("Ya existen pagarés para contrato " + idContratoCredito);
        }
        List<Pagare> pagares = new ArrayList<>();
        BigDecimal cuotaMensual = calcularCuotaMensual(montoSolicitado, tasaAnual, plazoMeses);
        for (int i = 1; i <= plazoMeses; i++) {
            Pagare p = new Pagare();
            p.setIdContratoCredito(idContratoCredito);
            p.setNumeroCuota((long) i);
            p.setMontoCuota(cuotaMensual);
            p.setFechaVencimiento(fechaInicio.plusMonths(i - 1));
            p.setEstado(PagareEstado.PENDIENTE);
            p.setVersion(1L);
            pagares.add(pagareRepository.save(p));
        }
        return pagareMapper.toDtoList(pagares);
    }

    @Transactional
    public List<PagareDTO> generarPagaresDesdeContrato(Long idContratoCredito) {
        ContratoCredito contrato = contratoCreditoRepository.findById(idContratoCredito)
            .orElseThrow(() -> new PagareGenerationException("Contrato de crédito no encontrado: " + idContratoCredito));

        BigDecimal montoSolicitado = contrato.getMontoAprobado();

        BigDecimal tasaAnual = contrato.getTasaEfectivaAnual();
        int plazoMeses = contrato.getPlazoFinalMeses().intValue();
        LocalDate fechaInicio = contrato.getFechaGeneracion().toLocalDate(); // Ajusta si tienes un campo específico para fecha de inicio

    return generarPagaresDesdeParams(idContratoCredito, montoSolicitado, tasaAnual, plazoMeses, fechaInicio);
    }


    private BigDecimal calcularCuotaMensual(BigDecimal monto, BigDecimal tasaAnual, int plazoMeses) {
        if (tasaAnual == null || tasaAnual.compareTo(BigDecimal.ZERO) <= 0) {
            return monto.divide(BigDecimal.valueOf(plazoMeses), 2, RoundingMode.HALF_UP);
        }
    BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(100L * 12L), 10, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(tasaMensual).pow(plazoMeses);
        BigDecimal numerador = monto.multiply(tasaMensual).multiply(factor);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    public boolean existenPagaresPorContrato(Long idContratoCredito) {
        return pagareRepository.existsByIdContratoCredito(idContratoCredito);
    }

    /**
     * Dado el idContratoCredito, obtiene el numeroSolicitud asociado usando el idSolicitud y el MS de originación
     */
    public String obtenerNumeroSolicitudPorIdContrato(Long idContratoCredito) {
        Optional<ContratoCredito> contratoOpt = contratoCreditoRepository.findById(idContratoCredito);
        if (contratoOpt.isEmpty()) {
            throw new IllegalArgumentException(CONTRATO_NO_ENCONTRADO + idContratoCredito);
        }
        ContratoCredito contrato = contratoOpt.get();
        String numeroSolicitud = contrato.getNumeroSolicitud();
        if (numeroSolicitud == null || numeroSolicitud.isEmpty()) {
            throw new IllegalStateException("El contrato no tiene numeroSolicitud asociado");
        }
        return numeroSolicitud;
    }

    // Utilidad para evitar nulls en extras

    @Transactional
    public ContratoCreditoDTO updateContratoCredito(Long id, ContratoCreditoUpdateDTO dto) {
        if (!id.equals(dto.getIdContratoCredito())) {
            throw new ContratoCreditoGenerationException("El ID del path no coincide con el del body");
        }
        ContratoCredito existing = contratoCreditoRepository.findById(id)
            .orElseThrow(() -> new ContratoCreditoGenerationException(CONTRATO_NO_ENCONTRADO + id));
        contratoCreditoMapper.updateEntity(existing, dto);
        ContratoCredito updated = contratoCreditoRepository.save(existing);
        return contratoCreditoMapper.toDto(updated);
    }
}
