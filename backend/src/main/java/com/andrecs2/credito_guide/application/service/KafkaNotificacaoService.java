package com.andrecs2.credito_guide.application.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.andrecs2.credito_guide.application.ports.service.KafkaNotificacaoServiceAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class KafkaNotificacaoService implements KafkaNotificacaoServiceAdapter {

	private static final Logger logger = LoggerFactory.getLogger(KafkaNotificacaoService.class);
	@Autowired
	private  KafkaTemplate<String, String> kafkaTemplate;
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${kafka.topic.consulta:consulta-realizada}")
	private String topicConsulta;

	@Value("${kafka.bootstrap-servers:}")
	private String bootstrapServers;

	@PostConstruct
	public void init() {
		logger.info("KafkaProducerService inicializado");
		logger.info("Topic: {}", topicConsulta);
		logger.info("Bootstrap servers: {}", bootstrapServers);
	}

	private void logKafkaStatus() {
			boolean configurado = isKafkaConfigurado();
		boolean templateDisponivel = kafkaTemplate != null;

		if (!configurado) {
			logger.info("ℹ️  KafkaProducerService: Kafka NÃO CONFIGURADO");
			logger.info("   Notificações serão apenas logadas");
		} else if (!templateDisponivel) {
			logger.warn("⚠️  KafkaProducerService: Kafka CONFIGURADO mas template não disponível");
		} else {
			logger.info("✅ KafkaProducerService: PRONTO para enviar notificações");
			logger.info("   Tópico: {}, Servers: {}", topicConsulta, bootstrapServers);
		}
	}

	public void enviarNotificacaoConsulta(Long creditoId, String cpfOuNfse, String acao) {
		Map<String, Object> mensagem = criarMensagemConsulta(creditoId, cpfOuNfse, acao);
		enviarMensagem(String.valueOf(creditoId), mensagem, acao, cpfOuNfse);
	}

	public void enviarNotificacaoConsulta(String numeroCredito, String numeroNfse, String acao) {
		Map<String, Object> mensagem = new HashMap<>();
		mensagem.put("evento", "CONSULTA_CREDITO");
		mensagem.put("numeroCredito", numeroCredito);
		mensagem.put("numeroNfse", numeroNfse);
		mensagem.put("acao", acao);
		mensagem.put("timestamp", System.currentTimeMillis());
		mensagem.put("servico", "credito-guide");

		enviarMensagem(numeroCredito, mensagem, acao, numeroNfse);
	}

	public void enviarNotificacao(String chave, String tipoEvento, Map<String, Object> dados) {
		boolean kafkaPronto = isKafkaConfigurado() && kafkaTemplate != null;

		if (!kafkaPronto) {
			System.out.println("KafkaProducerService.enviarNotificacao()+1");
			logger.info("📝 [FALLBACK] Notificação - Chave: {}, Evento: {}", chave, tipoEvento);
			return;
		}

		try {
			Map<String, Object> mensagem = new HashMap<>();
			mensagem.put("evento", tipoEvento);
			mensagem.put("timestamp", System.currentTimeMillis());
			mensagem.putAll(dados);

			String mensagemJson = objectMapper.writeValueAsString(mensagem);
			kafkaTemplate.send(topicConsulta, chave, mensagemJson);

			logger.debug("✅ Kafka: Notificação genérica enviada - Chave: {}", chave);

		} catch (Exception e) {
			logger.error("❌ Erro ao enviar notificação genérica: {}", e.getMessage());
		}
	}

	private void enviarMensagem(String chave, Map<String, Object> mensagem, String acao, String identificador) {
		boolean kafkaPronto = isKafkaConfigurado() && kafkaTemplate != null;

		if (!kafkaPronto) {
			logger.info("📝 [FALLBACK] Notificação - {}: {}, Identificador: {}", acao, chave, identificador);
			return;
		}

		try {
			String mensagemJson = objectMapper.writeValueAsString(mensagem);

			kafkaTemplate.send(topicConsulta, chave, mensagemJson).whenComplete((result, ex) -> {
				if (ex == null) {
					logger.debug("✅ Kafka: Notificação enviada - {}: {}", acao, chave);
				} else {
					System.out.println("KafkaProducerService.enviarMensagem()");
					logger.warn("⚠️  Kafka: Falha ao enviar - {}: {}, Erro: {}", acao, chave, ex.getMessage());
					logger.info("📝 [FALLBACK] Notificação - {}: {}", acao, chave);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("❌ Erro ao criar notificação JSON: {}", e.getMessage());
			logger.info("📝 [FALLBACK] Notificação - {}: {}", acao, chave);
		}
	}

	public boolean testarConexao() {
		if (!isKafkaConfigurado() || kafkaTemplate == null) {
			return false;
		}

		try {
			var partitions = kafkaTemplate.partitionsFor(topicConsulta);
			return partitions != null;
		} catch (Exception e) {
			logger.warn("❌ Teste de conexão Kafka falhou: {}", e.getMessage());
			return false;
		}
	}

	private Map<String, Object> criarMensagemConsulta(Long creditoId, String cpfOuNfse, String acao) {
		Map<String, Object> mensagem = new HashMap<>();
		mensagem.put("evento", "CONSULTA_CREDITO");
		mensagem.put("creditoId", creditoId);
		mensagem.put("identificador", cpfOuNfse);
		mensagem.put("acao", acao);
		mensagem.put("timestamp", System.currentTimeMillis());
		mensagem.put("servico", "credito-guide");
		mensagem.put("versao", "1.0.0");
		return mensagem;
	}

	private boolean isKafkaConfigurado() {
		return bootstrapServers != null && !bootstrapServers.trim().isEmpty()
				&& !bootstrapServers.equals("${KAFKA_BOOTSTRAP_SERVERS}");
	}
}