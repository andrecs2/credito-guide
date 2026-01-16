package com.andrecs2.credito_guide.application.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.andrecs2.credito_guide.application.entity.converter.SimNaoConverter;
import com.andrecs2.credito_guide.application.entity.enums.SimNao;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "credito")
public class Credito implements Serializable {

	private static final long serialVersionUID = 3094308530590202571L;
	@Id
	@Column(name = "id_credito")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "numero_credito")
	private String numeroCredito;

	@Column(name = "numero_nfse")
	private String numeroNfse;

	@Column(name = "data_constituicao", nullable = false)
	private LocalDate dataConstituicao;

	@Column(name = "valor_issqn", precision = 15, scale = 2, nullable = false)
	private BigDecimal valorIssqn;
	
	@Column(name = "tipo_credito", nullable = false)
	private String tipoCredito;

	@Column(name = "simples_nacional", nullable = false)
	@Convert(converter = SimNaoConverter.class)
	private SimNao simplesNacional;

	@Column(name = "aliquota", precision = 15, scale = 2, nullable = false)
	private BigDecimal aliquota;
	
	@Column(name = "valor_faturado", precision = 15, scale = 2, nullable = false)
	private BigDecimal valorFaturado;
	@Column(name = "valor_deducao", precision = 15, scale = 2, nullable = false)
	private BigDecimal valorDeducao;
	@Column(name = "base_calculo", precision = 15, scale = 2, nullable = false)
	private BigDecimal baseCalculo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumeroCredito() {
		return numeroCredito;
	}

	public void setNumeroCredito(String numeroCredito) {
		this.numeroCredito = numeroCredito;
	}

	public String getNumeroNfse() {
		return numeroNfse;
	}

	public void setNumeroNfse(String numeroNfse) {
		this.numeroNfse = numeroNfse;
	}




	public LocalDate getDataConstituicao() {
		return dataConstituicao;
	}

	public void setDataConstituicao(LocalDate dataConstituicao) {
		this.dataConstituicao = dataConstituicao;
	}

	public BigDecimal getValorIssqn() {
		return valorIssqn;
	}

	public void setValorIssqn(BigDecimal valorIssqn) {
		this.valorIssqn = valorIssqn;
	}

	public String getTipoCredito() {
		return tipoCredito;
	}

	public void setTipoCredito(String tipoCredito) {
		this.tipoCredito = tipoCredito;
	}

	public SimNao getSimplesNacional() {
		return simplesNacional;
	}

	public void setSimplesNacional(SimNao simplesNacional) {
		this.simplesNacional = simplesNacional;
	}

	public BigDecimal getAliquota() {
		return aliquota;
	}

	public void setAliquota(BigDecimal aliquota) {
		this.aliquota = aliquota;
	}

	public BigDecimal getValorFaturado() {
		return valorFaturado;
	}

	public void setValorFaturado(BigDecimal valorFaturado) {
		this.valorFaturado = valorFaturado;
	}

	public BigDecimal getValorDeducao() {
		return valorDeducao;
	}

	public void setValorDeducao(BigDecimal valorDeducao) {
		this.valorDeducao = valorDeducao;
	}

	public BigDecimal getBaseCalculo() {
		return baseCalculo;
	}

	public void setBaseCalculo(BigDecimal baseCalculo) {
		this.baseCalculo = baseCalculo;
	}

}
