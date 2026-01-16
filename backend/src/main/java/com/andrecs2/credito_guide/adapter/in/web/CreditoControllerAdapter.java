package com.andrecs2.credito_guide.adapter.in.web;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andrecs2.credito_guide.application.ports.service.CreditoServiceAdapter;
import com.andrecs2.credito_guide.infra.response.CreditoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/creditos")
@CrossOrigin(origins = "*")
@Tag(name = "API de Consulta de Créditos")

public class CreditoControllerAdapter {


	@Autowired
	private CreditoServiceAdapter adapter;


    @GetMapping(value = "/{numeroNfse}", produces = "application/json")
    @Operation(summary = "Consultar créditos por número da NFS-e")
    public ResponseEntity<Page<CreditoResponse>> consultarPorNfse(
            @Parameter(description = "Número da NFS-e", required = true, example = "7891011")
            @PathVariable String numeroNfse) {

        Page<CreditoResponse> creditos = adapter.findByNumeroNfse(numeroNfse);
        return ResponseEntity.ok(creditos);
    }

    @GetMapping(value = "/credito/{numeroCredito}", produces = "application/json")
    @Operation(summary = "Consultar crédito por número do crédito")
    public ResponseEntity<Optional<CreditoResponse>> consultarPorNumeroCredito(
            @Parameter(description = "Número do crédito", required = true) @PathVariable String numeroCredito) {
        Optional<CreditoResponse> credito = adapter.findByNumeroCredito(numeroCredito);
        return ResponseEntity.ok(credito);
    }
}