package com.andrecs2.credito_guide.adapter.converter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.andrecs2.credito_guide.application.entity.Credito;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@Component
public class CreditoConverter {

	public CreditoResponse toResponse(Credito entity) {
		// TODO Auto-generated method stub
		return null;
	}

	public CreditoResponse toResponse(Optional<Credito> entity) {
		Credito credito = entity.orElseThrow(() -> new IllegalArgumentException("Crédito não encontrado"));
		return toResponse(credito);
	}

}
