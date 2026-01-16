package com.andrecs2.credito_guide.application.ports.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.andrecs2.credito_guide.infra.response.CreditoResponse;

;

public interface CreditoRepositoryAdapter {

	Optional<CreditoResponse> findByNumeroCredito(String numeroCredito);

	Page<CreditoResponse> findByNumeroNfse(String numeroNfse);
 
}
