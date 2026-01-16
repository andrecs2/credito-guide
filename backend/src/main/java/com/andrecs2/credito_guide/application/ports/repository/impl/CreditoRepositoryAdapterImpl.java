package com.andrecs2.credito_guide.application.ports.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.adapter.converter.CreditoConverter;
import com.andrecs2.credito_guide.application.entity.Credito;
import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.ports.repository.jpa.CreditoRepository;
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
		return Optional.of(converter.toResponse(repository.findByNumeroCredito(numeroCredito)));
	}

	@Override
	public Page<CreditoResponse> findByNumeroNfse(String numeroNfse) {
		return toList(repository.findAll((Specification<Credito>) (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("numeroNfse"), numeroNfse));

			return builder.and(predicates.toArray(new Predicate[0]));
		}, PageRequest.of(10, 20)));
	}

	
	protected Page<CreditoResponse> toList(Page<Credito> all) {
		List<CreditoResponse> list = Collections.emptyList();
		int number = 0;
		int size = 10;
		Sort sort = null;
		long total = 0;
		if (all != null) {
			list = all.getContent()
					.stream()
					.map(converter::toResponse)
					.collect(Collectors.toList());

			number = all.getNumber();
			size = all.getSize();
			sort = all.getSort();
			total = all.getTotalElements();
		}
		return new PageImpl<CreditoResponse>(list, PageRequest.of(number, size, sort), total);
	}
}
