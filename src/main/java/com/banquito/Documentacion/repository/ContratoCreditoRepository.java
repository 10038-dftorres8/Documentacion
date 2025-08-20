package com.banquito.Documentacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.Documentacion.enums.ContratoCreditoEstado;
import com.banquito.Documentacion.model.ContratoCredito;

@Repository
public interface ContratoCreditoRepository extends JpaRepository<ContratoCredito, Long> {

    Optional<ContratoCredito> findByNumeroSolicitud(String numeroSolicitud);
    Optional<ContratoCredito> findByNumeroContrato(String numeroContrato);
    List<ContratoCredito> findByEstado(ContratoCreditoEstado estado);
    boolean existsByNumeroSolicitud(String numeroSolicitud);
    boolean existsByNumeroContrato(String numeroContrato);

    Page<ContratoCredito> findByEstado(ContratoCreditoEstado estado, Pageable pageable);
    Page<ContratoCredito> findByNumeroSolicitud(String numeroSolicitud, Pageable pageable);
    Page<ContratoCredito> findByNumeroContratoContainingIgnoreCase(String numeroContrato, Pageable pageable);

    Page<ContratoCredito> findByEstadoAndNumeroContratoContainingIgnoreCase(
        ContratoCreditoEstado estado, String numeroContrato, Pageable pageable);

    Page<ContratoCredito> findByEstadoAndNumeroSolicitud(
        ContratoCreditoEstado estado, String numeroSolicitud, Pageable pageable);

    Page<ContratoCredito> findByNumeroContratoContainingIgnoreCaseAndNumeroSolicitud(
        String numeroContrato, String numeroSolicitud, Pageable pageable);

    Page<ContratoCredito> findByEstadoAndNumeroContratoContainingIgnoreCaseAndNumeroSolicitud(
        ContratoCreditoEstado estado, String numeroContrato, String numeroSolicitud, Pageable pageable);
}