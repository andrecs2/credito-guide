package com.andrecs2.credito_guide.application.ports.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.andrecs2.credito_guide.application.entity.Credito;
@Repository
public interface CreditoRepository extends JpaRepository<Credito, Integer>, JpaSpecificationExecutor<Credito> {

	Optional<Credito> findByNumeroCredito(String numeroCredito);

}
