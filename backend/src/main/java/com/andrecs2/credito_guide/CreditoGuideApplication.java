package com.andrecs2.credito_guide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.andrecs2.credito_guide"})
public class CreditoGuideApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditoGuideApplication.class, args);
	}

}
