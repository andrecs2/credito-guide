package com.andrecs2.credito_guide.adapter.converter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.andrecs2.credito_guide.application.entity.Credito;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@Component
public class CreditoConverter {

	public CreditoResponse toResponse(Credito entity) {
		if(entity != null) {
			return new CreditoResponse.Builder()
					.id(entity.getId())
					.numeroCredito(entity.getNumeroCredito())
					.numeroNfse(entity.getNumeroNfse())
					.dataConstituicao(entity.getDataConstituicao().toString())
					.valorIssqn(entity.getValorIssqn().doubleValue())
					.tipoCredito(entity.getTipoCredito())
					.simplesNacional(entity.getSimplesNacional())
					.aliquota(entity.getAliquota().doubleValue())
					.valorFaturado(entity.getValorFaturado().doubleValue())
					.valorDeducao(entity.getValorDeducao().doubleValue())
					.baseCalculo(entity.getBaseCalculo().doubleValue())
					.build();
		}
		return null;
	}

	public CreditoResponse toResponse(Optional<Credito> entity) {
		Credito credito = entity.orElseThrow(() -> new IllegalArgumentException("Crédito não encontrado"));
		return toResponse(credito);
	}

}
