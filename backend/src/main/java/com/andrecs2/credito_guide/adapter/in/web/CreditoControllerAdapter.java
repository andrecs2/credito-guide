package com.andrecs2.credito_guide.adapter.in.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creditos")
@CrossOrigin(origins = "*")
public class CreditoControllerAdapter {


    String retorno = "primeiro_teste";

    @GetMapping(value="/{numeroNfse}",  produces = "application/json")
    public ResponseEntity<List<String>> consultarPorNfse(
            @PathVariable String numeroNfse) {

        List<String> teste = Arrays.asList(new String[] {  retorno });

        return ResponseEntity.ok(teste);
    }

    @GetMapping(value="/credito/{numeroCredito}",     produces = "application/json" )
    public ResponseEntity<String> consultarPorNumeroCredito(
            @PathVariable String numeroCredito) {

        return ResponseEntity.ok(retorno);
    }
}