package com.andrecs2.credito_guide.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.ports.service.CreditoServiceAdapter;
import com.andrecs2.credito_guide.application.ports.service.KafkaNotificacaoServiceAdapter;
import com.andrecs2.credito_guide.domain.exception.CreditoNotFoundException;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@Service
public class CreditoService implements CreditoServiceAdapter {
	
    private static final Logger logger = LoggerFactory.getLogger(CreditoService.class);


	@Autowired
	private CreditoRepositoryAdapter repositoryAdapter;
	
	@Autowired
	private KafkaNotificacaoServiceAdapter kafkaProducerService;

	@Value("${kafka.bootstrap-servers:}")
	private String topicConsulta;

	@Override
	public List<CreditoResponse> findByNumeroNfse(String numeroNfse) {

		logger.info("Consultando créditos por NFS-e: {}", numeroNfse);
		
		 return Optional.ofNullable(repositoryAdapter.findByNumeroNfse(numeroNfse))
        .filter(list -> !list.isEmpty())
        .map(creditos -> {
            creditos.forEach(credito -> 
                kafkaProducerService.enviarNotificacaoConsulta(
                    credito.getNumeroCredito(), 
                    credito.getNumeroNfse(),
                    "CONSULTA_NFSE"
                )
            );
            return creditos;
        })
        .orElseGet(() -> {
            kafkaProducerService.enviarNotificacaoConsulta(
                "",
                numeroNfse,
                "CONSULTA_NFSE_NAO_ENCONTRADO"
            );
            return Collections.emptyList();
        });
	}
	
	@Override
	public Optional<CreditoResponse> findByNumeroCredito(String numeroCredito) {
		logger.info("Consultando crédito por número: {}", numeroCredito);

		try {
			CreditoResponse credito = repositoryAdapter.findByNumeroCredito(numeroCredito)
					.orElseThrow(() -> new CreditoNotFoundException("Crédito não encontrado: " + numeroCredito));

			kafkaProducerService.enviarNotificacaoConsulta(
				credito.getNumeroCredito(),
				credito.getNumeroNfse(),
				"CONSULTA_CREDITO"
			);
			return Optional.of(credito);
		} catch (CreditoNotFoundException ex) {
			kafkaProducerService.enviarNotificacaoConsulta(
				numeroCredito,
				"",
				"CONSULTA_CREDITO_NAO_ENCONTRADO"
			);
			throw ex;
		}
	}
}
