package com.andrecs2.credito_guide.application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.ports.service.CreditoServiceAdapter;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@Service
public class CreditoService implements CreditoServiceAdapter {

	@Autowired
	private CreditoRepositoryAdapter repositoryAdapter;

	@Override
	public Page<CreditoResponse> findByNumeroNfse(String numeroNfse) {

		return repositoryAdapter.findByNumeroNfse(numeroNfse);

	}

	@Override
	public Optional<CreditoResponse> findByNumeroCredito(String numeroCredito) {
		return repositoryAdapter.findByNumeroCredito(numeroCredito);
	}

}
