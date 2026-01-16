package com.andrecs2.credito_guide.domain.exception;

public class CreditoNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 364460447931730486L;

	public CreditoNotFoundException(String message) {
		super(message);
	}

	public CreditoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public static CreditoNotFoundException byNumeroCredito(String numeroCredito) {
		return new CreditoNotFoundException(String.format("Crédito não encontrado com número: %s", numeroCredito));
	}

	public static CreditoNotFoundException byNumeroNfse(String numeroNfse) {
		return new CreditoNotFoundException(String.format("Nenhum crédito encontrado para a NFS-e: %s", numeroNfse));
	}
}