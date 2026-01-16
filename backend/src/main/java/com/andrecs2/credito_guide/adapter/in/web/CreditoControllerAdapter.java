package com.andrecs2.credito_guide.adapter.in.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/creditos")
@CrossOrigin(origins = "*")
@Tag(name = "API de Consulta de Créditos")

public class CreditoControllerAdapter {

    String retorno = "primeiro_teste";

    @GetMapping(value = "/{numeroNfse}", produces = "application/json")
    @Operation(summary = "Consultar créditos por número da NFS-e")

    public ResponseEntity<List<String>> consultarPorNfse(
            @Parameter(description = "Número da NFS-e", required = true, example = "7891011")

            @PathVariable String numeroNfse) {

        List<String> teste = Arrays.asList(new String[] { retorno });

        return ResponseEntity.ok(teste);
    }

    @GetMapping(value = "/credito/{numeroCredito}", produces = "application/json")
    @Operation(summary = "Consultar crédito por número do crédito")

    public ResponseEntity<String> consultarPorNumeroCredito(
            @Parameter(description = "Número do crédito", required = true) @PathVariable String numeroCredito) {

        return ResponseEntity.ok(retorno);
    }
}