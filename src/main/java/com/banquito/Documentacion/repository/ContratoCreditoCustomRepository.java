package com.banquito.Documentacion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.Documentacion.model.ContratoCredito;

@Repository
public interface ContratoCreditoCustomRepository extends JpaRepository<ContratoCredito, Long> {
    Optional<ContratoCredito> findByIdContratoCredito(Long idContratoCredito);
}
