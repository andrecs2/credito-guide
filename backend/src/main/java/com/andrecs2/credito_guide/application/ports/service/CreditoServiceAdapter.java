package com.andrecs2.credito_guide.application.ports.service;

import java.util.List;

import com.andrecs2.credito_guide.infra.response.CreditoResponse;

public interface CreditoServiceAdapter {

    List<CreditoResponse> findByNumeroNfse(String numeroNfse);

    CreditoResponse findByNumeroCredito(String numeroCredito);

}
