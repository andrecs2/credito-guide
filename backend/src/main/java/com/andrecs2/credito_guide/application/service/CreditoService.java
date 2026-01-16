package com.andrecs2.credito_guide.application.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.application.ports.service.CreditoServiceAdapter;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;
import com.andrecs2.credito_guide.util.LeitorJson;
@Service
public class CreditoService implements CreditoServiceAdapter {


	@Override
	public List<CreditoResponse> findByNumeroNfse(String numeroNfse) {
		CreditoResponse credito = LeitorJson.getMock();
		return Arrays.asList(credito);
	}

	@Override
	public CreditoResponse findByNumeroCredito(String numeroCredito) {
		CreditoResponse credito = LeitorJson.getMock();
		return credito;
	}

}
