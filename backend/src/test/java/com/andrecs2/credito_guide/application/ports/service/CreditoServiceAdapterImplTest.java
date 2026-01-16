package com.andrecs2.credito_guide.application.ports.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.service.CreditoService;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@ExtendWith(MockitoExtension.class)
class CreditoServiceAdapterImplTest {

    @Mock
    private CreditoRepositoryAdapter repository;

    @InjectMocks
    private CreditoService service;

    @Test
    @DisplayName("Deve retornar créditos quando NFS-e existir")
    void deveRetornarCreditosPorNfse() {
        String nfse = "7891011";

        CreditoResponse response = new CreditoResponse.Builder()
                .numeroCredito("123456")
                .numeroNfse(nfse)
                .valorFaturado(new BigDecimal("150.00").doubleValue())
                .build();

        Page<CreditoResponse> page = new PageImpl<>(List.of(response));

        when(repository.findByNumeroNfse(nfse)).thenReturn(page);

        Page<CreditoResponse> resultado = service.findByNumeroNfse(nfse);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals("123456", resultado.getContent().get(0).getNumeroCredito());

        verify(repository).findByNumeroNfse(nfse);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando NFS-e não existir")
    void deveRetornarPaginaVaziaQuandoNfseNaoExistir() {
        String nfse = "000000";

        when(repository.findByNumeroNfse(nfse)).thenReturn(Page.empty());

        Page<CreditoResponse> resultado = service.findByNumeroNfse(nfse);

        assertTrue(resultado.isEmpty());
        verify(repository).findByNumeroNfse(nfse);
    }

    @Test
    @DisplayName("Deve retornar crédito quando número existir")
    void deveRetornarCreditoPorNumero() {
        String numeroCredito = "123456";

        CreditoResponse response = new CreditoResponse.Builder()
                .numeroCredito(numeroCredito)
                .numeroNfse("7891011")
                .valorDeducao(new BigDecimal("200.00").doubleValue())
                .build();

        when(repository.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.of(response));

        Optional<CreditoResponse> resultado =
                service.findByNumeroCredito(numeroCredito);

        assertTrue(resultado.isPresent());
        assertEquals(numeroCredito, resultado.get().getNumeroCredito());

        verify(repository).findByNumeroCredito(numeroCredito);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando crédito não existir")
    void deveRetornarVazioQuandoCreditoNaoExistir() {
        String numeroCredito = "999999";

        when(repository.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.empty());

        Optional<CreditoResponse> resultado =
                service.findByNumeroCredito(numeroCredito);

        assertTrue(resultado.isEmpty());
        verify(repository).findByNumeroCredito(numeroCredito);
    }
}
