package com.andrecs2.credito_guide.util;

import java.io.InputStream;

import com.andrecs2.credito_guide.infra.response.CreditoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LeitorJson {

	public static CreditoResponse getMock(){
		InputStream is =CreditoResponse.class
				.getClassLoader()
				.getResourceAsStream("credito.json");
		ObjectMapper mapper = new ObjectMapper();
		
		CreditoResponse credito = null;
		try {
			credito = mapper.readValue(is, CreditoResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return credito;
	}

	public static void main(String[] args) throws Exception {

		CreditoResponse credito = getMock();

		System.out.println(credito.getNumeroCredito());
		System.out.println(credito);
	}
    
}
