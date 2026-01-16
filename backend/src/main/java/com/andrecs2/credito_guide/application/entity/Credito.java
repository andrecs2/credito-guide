package com.andrecs2.credito_guide.application.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table( name = "credito")
public class Credito implements Serializable{

	
	private static final long serialVersionUID = 3094308530590202571L;
	@Id
	@Column(name = "id_credito")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	@Column(name = "numero_credito")
	private String numeroCredito;
	
	@Column(name = "numero_nfse")
	private String numeroNfse;
	
}
