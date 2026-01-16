package com.andrecs2.credito_guide.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.andrecs2.credito_guide.infra.response.CreditoResponse;
import com.andrecs2.credito_guide.util.LeitorJson;

@WebMvcTest(CreditoControllerAdapter.class)
class CreditoControllerAdapterTest {

	@Autowired
	private MockMvc mockMvc;

	CreditoResponse creditoMock = LeitorJson.getMock();

	@BeforeEach
	void setUp() {

	}

	@Test
	void testConsultarPorNfse_Success() throws Exception {

		mockMvc.perform(get("/api/creditos/7891011")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].numeroCredito").value(creditoMock.getNumeroCredito()))
				.andExpect(jsonPath("$[0].numeroNfse").value(creditoMock.getNumeroNfse()))
				.andExpect(jsonPath("$[0].simplesNacional").value(creditoMock.getSimplesNacional()))
				.andExpect(jsonPath("$[0].aliquota").value(creditoMock.getAliquota()))
				.andExpect(jsonPath("$[0].valorIssqn").value(creditoMock.getValorIssqn()));
	}

	@Test
	void testConsultarPorNumeroCredito_Success() throws Exception {
		mockMvc.perform(get("/api/creditos/credito/123456")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numeroCredito").value(creditoMock.getNumeroCredito()))
				.andExpect(jsonPath("$.numeroNfse").value(creditoMock.getNumeroNfse()))
				.andExpect(jsonPath("$.simplesNacional").value(creditoMock.getSimplesNacional()))
				.andExpect(jsonPath("$.tipoCredito").value(creditoMock.getTipoCredito()));
	}

//    @Test
//    void testConsultarPorNfse_NotFound() throws Exception {
//        
//        // Act & Assert
//        mockMvc.perform(get("/api/creditos/999999")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//    }
}