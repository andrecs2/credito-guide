package com.andrecs2.credito_guide.adapter.in.web;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CreditoControllerAdapter.class)
class CreditoControllerAdapterTest {
    
    @Autowired
    private MockMvc mockMvc;
    
        
    String retorno = "primeiro_teste";
    
    @BeforeEach
    void setUp() {
       
    }
    
    @Test
    void testConsultarPorNfse_Success() throws Exception {
        List<String> creditos = Arrays.asList(retorno);
        
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(retorno));
    }
    
    @Test
    void testConsultarPorNumeroCredito_Success() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(retorno));
    }
    
    @Test
    void testConsultarPorNfse_NotFound() throws Exception {
        
        // Act & Assert
        mockMvc.perform(get("/api/creditos/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}