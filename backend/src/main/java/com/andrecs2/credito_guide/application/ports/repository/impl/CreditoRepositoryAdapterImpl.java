package com.andrecs2.credito_guide.application.ports.repository.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;
import com.andrecs2.credito_guide.util.LeitorJson;


@Service
public class CreditoRepositoryAdapterImpl implements CreditoRepositoryAdapter {

	CreditoResponse credito = LeitorJson.getMock();
	
	@Override
	public Optional<CreditoResponse> findByNumeroCredito(String numeroCredito) {
		return Optional.of(credito);
	}

	@Override
	public Page<CreditoResponse> findByNumeroNfse(String numeroNfse) {
		return new PageImpl<CreditoResponse>(Arrays.asList(credito));
	}

}
