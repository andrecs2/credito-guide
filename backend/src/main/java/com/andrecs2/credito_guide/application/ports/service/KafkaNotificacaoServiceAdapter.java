package com.andrecs2.credito_guide.application.ports.service;


public interface KafkaNotificacaoServiceAdapter {

	void enviarNotificacaoConsulta(String numeroCredito, String numeroNfse, String string);

}
