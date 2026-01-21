package com.andrecs2.credito_guide.application.ports.repository;

import java.util.List;
import java.util.Optional;

import com.andrecs2.credito_guide.infra.response.CreditoResponse;

;

public interface CreditoRepositoryAdapter {

	Optional<CreditoResponse> findByNumeroCredito(String numeroCredito);

	List<CreditoResponse> findByNumeroNfse(String numeroNfse);
 
}
