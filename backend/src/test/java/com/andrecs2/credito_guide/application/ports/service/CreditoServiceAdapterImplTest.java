package com.andrecs2.credito_guide.application.ports.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.service.CreditoService;
import com.andrecs2.credito_guide.domain.exception.CreditoNotFoundException;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@ExtendWith(MockitoExtension.class)
class CreditoServiceAdapterImplTest {

    @Mock
    private CreditoRepositoryAdapter repository;

    @InjectMocks
    private CreditoService service;
    

    @Mock
    private KafkaNotificacaoServiceAdapter kafkaProducerService;

    @Test
    @DisplayName("Deve retornar créditos quando NFS-e existir")
    void deveRetornarCreditosPorNfse() {
    	String nfse = "7891011";

        CreditoResponse response = new CreditoResponse.Builder()
                .numeroCredito("123456")
                .numeroNfse(nfse)
                .valorFaturado(new BigDecimal("150.00").doubleValue())
                .build();

        when(repository.findByNumeroNfse(nfse))
                .thenReturn(List.of(response));

        List<CreditoResponse> resultado = service.findByNumeroNfse(nfse);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("123456", resultado.get(0).getNumeroCredito());

        verify(repository).findByNumeroNfse(nfse);
        verify(kafkaProducerService).enviarNotificacaoConsulta(
                "123456",
                nfse,
                "CONSULTA_NFSE"
        );
    }

    @Test
    @DisplayName("Deve retornar página vazia quando NFS-e não existir")
    void deveRetornarPaginaVaziaQuandoNfseNaoExistir() {
    	  String nfse = "000000";

    	    when(repository.findByNumeroNfse(nfse))
    	            .thenReturn(Collections.emptyList());

    	    List<CreditoResponse> resultado = service.findByNumeroNfse(nfse);

    	    assertTrue(resultado.isEmpty());

    	    verify(repository).findByNumeroNfse(nfse);
    	    verify(kafkaProducerService).enviarNotificacaoConsulta(
    	            "",
    	            nfse,
    	            "CONSULTA_NFSE_NAO_ENCONTRADO"
    	    );
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
    @DisplayName("Deve lançar exceção quando crédito não existir")
    void deveLancarExcecaoQuandoCreditoNaoExistir() {
        String numeroCredito = "999999";

        when(repository.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.empty());

        assertThrows(
            CreditoNotFoundException.class,
            () -> service.findByNumeroCredito(numeroCredito)
        );

        verify(repository).findByNumeroCredito(numeroCredito);
        verify(kafkaProducerService).enviarNotificacaoConsulta(
            numeroCredito,
            "",
            "CONSULTA_CREDITO_NAO_ENCONTRADO"
        );
    }
}
