package com.andrecs2.credito_guide.application.ports.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.andrecs2.credito_guide.application.entity.Credito;
import com.andrecs2.credito_guide.application.entity.enums.SimNao;
import com.andrecs2.credito_guide.application.service.KafkaNotificacaoService;

@DataJpaTest
@ActiveProfiles("test")
class CreditoRepositoryTest {

    @Autowired
    private CreditoRepository repository;

    @MockBean
    private KafkaNotificacaoService kafkaNotificacaoService;
    
    @Test
    @DisplayName("Deve buscar créditos usando Specification por número da NFS-e")
    void deveBuscarPorNumeroNfseComSpecification() {
        // given
        Credito credito1 = new Credito();
        credito1.setNumeroCredito("CRED-1");
        credito1.setNumeroNfse("7891011");
        credito1.setValorFaturado(new BigDecimal("100"));
        credito1.setDataConstituicao(LocalDate.now());
        credito1.setValorIssqn(new BigDecimal("100.444"));
        credito1.setTipoCredito("fiado");
        credito1.setSimplesNacional(SimNao.NAO);
        credito1.setAliquota(new BigDecimal("0.444"));
        credito1.setValorDeducao(new BigDecimal("100.444"));
        credito1.setBaseCalculo(new BigDecimal("0.444"));

        Credito credito2 = new Credito();
        credito2.setNumeroCredito("CRED-2");
        credito2.setNumeroNfse("999999");
        credito2.setValorFaturado(new BigDecimal("200"));
        credito2.setDataConstituicao(LocalDate.now());
        credito2.setValorIssqn(new BigDecimal("100.444"));
        credito2.setValorDeducao(new BigDecimal("100.444"));
        credito2.setTipoCredito("fiado");
        credito2.setSimplesNacional(SimNao.SIM);
        credito2.setAliquota(new BigDecimal("0.444"));
        credito2.setBaseCalculo(new BigDecimal("0.444"));

        repository.save(credito1);
        repository.save(credito2);

        Page<Credito> page = repository.findAll(
                
        		(root, query, cb) ->
                cb.equal(root.get("numeroNfse"), "7891011"),
                
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent().get(2).getNumeroCredito())
                .isEqualTo("CRED-1");
    }

    @Test
    @DisplayName("Deve retornar página vazia quando Specification não encontra dados")
    void deveRetornarPaginaVazia() {
        Page<Credito> page = repository.findAll(
        		 (root, query, cb) ->
                 cb.equal(root.get("numeroNfse"), "NAO_EXISTE"),
                PageRequest.of(0, 10)
        );

        assertThat(page).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar crédito por número do crédito")
    void deveBuscarPorNumeroCredito() {
        Credito entity = new Credito();
        entity.setNumeroCredito("ABC123");
        entity.setNumeroNfse("999999");
        entity.setValorDeducao(new BigDecimal("200.00"));
        
        entity.setValorFaturado(new BigDecimal("200"));
        entity.setDataConstituicao(LocalDate.now());
        entity.setValorIssqn(new BigDecimal("100.444"));
        entity.setTipoCredito("fiado");
        entity.setSimplesNacional(SimNao.SIM);
        entity.setAliquota(new BigDecimal("0.444"));
        entity.setBaseCalculo(new BigDecimal("0.444"));

        repository.save(entity);

        Optional<Credito> resultado =
                repository.findByNumeroCredito("ABC123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroNfse()).isEqualTo("999999");
    }

    @Test
    @DisplayName("Deve retornar vazio quando crédito não existir")
    void deveRetornarVazioQuandoNaoExistir() {
        Optional<Credito> resultado =
                repository.findByNumeroCredito("NAO_EXISTE");

        assertThat(resultado).isEmpty();
    }
}
