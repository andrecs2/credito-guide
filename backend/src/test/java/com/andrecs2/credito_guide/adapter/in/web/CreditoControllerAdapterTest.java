package com.andrecs2.credito_guide.adapter.in.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.andrecs2.credito_guide.application.ports.repository.CreditoRepositoryAdapter;
import com.andrecs2.credito_guide.application.service.KafkaNotificacaoService;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

@WebMvcTest(controllers = CreditoControllerAdapter.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JacksonAutoConfiguration.class)
@ActiveProfiles("test")
class CreditoControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoRepositoryAdapter adapter;

    @MockBean
    private KafkaNotificacaoService kafkaNotificacaoService;

    @MockBean
    private DataSource dataSource;
    
    @MockBean
    private JdbcTemplate jdbcTemplate;
    @Test
    @DisplayName("Deve retornar 200 e lista de créditos quando NFS-e existir")
    void deveRetornarCreditosPorNfse() throws Exception {
        String nfse = "7891011";

        CreditoResponse response = new CreditoResponse.Builder()
               .numeroCredito("123456")
               .numeroNfse(nfse)
              .valorFaturado(new BigDecimal("150.00").doubleValue()).build()
        ;

		List<CreditoResponse> page = new ArrayList<CreditoResponse>() {
			{
				add(response);
			}
		};
        when(adapter.findByNumeroNfse(nfse)).thenReturn(page);

        mockMvc.perform(get("/api/creditos/{nfse}", nfse)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].numeroCredito").value("123456"))
                .andExpect(jsonPath("$.[0].numeroNfse").value(nfse))
                .andExpect(jsonPath("$.[0].valorFaturado").value(150.00));
    }

    @Test
    @DisplayName("Deve retornar 0 quando NFS-e não possuir créditos")
    void deveRetornar404QuandoNfseNaoEncontrada() throws Exception {
        String nfse = "000000";

        when(adapter.findByNumeroNfse(nfse)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/creditos/{nfse}", nfse)
                        .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @DisplayName("Deve retornar 200 quando crédito existir")
    void deveRetornarCreditoPorNumero() throws Exception {
        String numeroCredito = "123456";

        CreditoResponse response = new CreditoResponse.Builder()
                .numeroCredito(numeroCredito)
                .numeroNfse("7891011")
                .valorDeducao(new BigDecimal("200.00").doubleValue())
        .build();

        when(adapter.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/creditos/credito/{numero}", numeroCredito)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCredito").value(numeroCredito))
                .andExpect(jsonPath("$.numeroNfse").value("7891011"))
                .andExpect(jsonPath("$.valorDeducao").value(200.00));
    }

    @Test
    @DisplayName("Deve retornar 404 quando crédito não existir")
    void deveRetornar404QuandoCreditoNaoEncontrado() throws Exception {
        String numeroCredito = "999999";

        when(adapter.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/creditos/credito/{numero}", numeroCredito)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
