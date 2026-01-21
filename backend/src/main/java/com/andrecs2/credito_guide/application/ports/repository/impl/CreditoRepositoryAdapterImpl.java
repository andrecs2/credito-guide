package com.andrecs2.credito_guide.application.ports.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.adapter.converter.CreditoConverter;
import com.andrecs2.credito_guide.application.entity.Credito;
import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.ports.repository.jpa.CreditoRepository;
import com.andrecs2.credito_guide.domain.exception.CreditoNotFoundException;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

import jakarta.persistence.criteria.Predicate;




@Service
public class CreditoRepositoryAdapterImpl implements CreditoRepositoryAdapter {

	@Autowired
	private CreditoConverter converter;
	
	@Autowired
	private CreditoRepository repository;
	
	@Override
	public Optional<CreditoResponse> findByNumeroCredito(String numeroCredito) {
		Optional<Credito> credito = repository.findByNumeroCredito(numeroCredito);
		if(credito.isEmpty()){
			throw CreditoNotFoundException.byNumeroCredito(numeroCredito);
		}
		return Optional.of(converter.toResponse(credito));
	}

	@Override
	public List<CreditoResponse> findByNumeroNfse(String numeroNfse) {
		return repository.findAll((Specification<Credito>) (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("numeroNfse"), numeroNfse));

			return builder.and(predicates.toArray(new Predicate[0]));
		}, PageRequest.of(0, 50)).getContent().stream().map(converter::toResponse)
				.collect(Collectors.toList());
	}

	
	
}
