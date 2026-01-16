package com.andrecs2.credito_guide.infra.response;

import java.io.Serializable;

public class CreditoResponse implements Serializable {

	private static final long serialVersionUID = -6905430722708346070L;

	private String numeroCredito;
	private String numeroNfse;
	private String dataConstituicao;
	private Double valorIssqn;
	private String tipoCredito;
	private String simplesNacional;
	private Double aliquota;
	private Double valorFaturado;
	private Double valorDeducao;
	private Double baseCalculo;
	
	
	public static class Builder {

		private String numeroCredito;
		private String numeroNfse;
		private String dataConstituicao;
		private Double valorIssqn;
		private String tipoCredito;
		private String simplesNacional;
		private Double aliquota;
		private Double valorFaturado;
		private Double valorDeducao;
		private Double baseCalculo;
		

		public Builder numeroCredito(String numeroCredito) {
			this.numeroCredito = numeroCredito;
			return this;
		}

		public Builder numeroNfse(String numeroNfse) {
			this.numeroNfse = numeroNfse;
			return this;
		}

		public Builder dataConstituicao(String dataConstituicao) {
			this.dataConstituicao = dataConstituicao;
			return this;
		}

		public Builder valorIssqn(Double valorIssqn) {
			this.valorIssqn = valorIssqn;
			return this;
		}
		public Builder tipoCredito(String tipoCredito) {
			this.tipoCredito = tipoCredito;
			return this;
		}
		public Builder simplesNacional(String simplesNacional) {
			this.simplesNacional = simplesNacional;
			return this;
		}
		public Builder aliquota(Double aliquota) {
			this.aliquota = aliquota;
			return this;
		}
		public Builder valorFaturado(Double valorFaturado) {
			this.valorFaturado = valorFaturado;
			return this;
		}
		public Builder valorDeducao(Double valorDeducao) {
			this.valorDeducao = valorDeducao;
			return this;
		}
		public Builder baseCalculo(Double baseCalculo) {
			this.baseCalculo = baseCalculo;
			return this;
		}

		public CreditoResponse build() {
			CreditoResponse bean = new CreditoResponse();
			bean.setNumeroCredito(this.numeroCredito);
			bean.setNumeroNfse(this.numeroNfse);
			bean.setDataConstituicao(this.dataConstituicao);
			bean.setValorIssqn(this.valorIssqn);
			bean.setTipoCredito(this.tipoCredito);
			bean.setSimplesNacional(this.simplesNacional);
			bean.setAliquota(this.aliquota);
			bean.setValorFaturado(this.valorFaturado);
			bean.setValorDeducao(this.valorDeducao);
			bean.setBaseCalculo(this.baseCalculo);
			
			
			return bean;
		}
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


	public String getDataConstituicao() {
		return dataConstituicao;
	}


	public void setDataConstituicao(String dataConstituicao) {
		this.dataConstituicao = dataConstituicao;
	}


	public Double getValorIssqn() {
		return valorIssqn;
	}


	public void setValorIssqn(Double valorIssqn) {
		this.valorIssqn = valorIssqn;
	}


	public String getTipoCredito() {
		return tipoCredito;
	}


	public void setTipoCredito(String tipoCredito) {
		this.tipoCredito = tipoCredito;
	}


	public String getSimplesNacional() {
		return simplesNacional;
	}


	public void setSimplesNacional(String simplesNacional) {
		this.simplesNacional = simplesNacional;
	}


	public Double getAliquota() {
		return aliquota;
	}


	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}


	public Double getValorFaturado() {
		return valorFaturado;
	}


	public void setValorFaturado(Double valorFaturado) {
		this.valorFaturado = valorFaturado;
	}


	public Double getValorDeducao() {
		return valorDeducao;
	}


	public void setValorDeducao(Double valorDeducao) {
		this.valorDeducao = valorDeducao;
	}


	public Double getBaseCalculo() {
		return baseCalculo;
	}


	public void setBaseCalculo(Double baseCalculo) {
		this.baseCalculo = baseCalculo;
	}
}
