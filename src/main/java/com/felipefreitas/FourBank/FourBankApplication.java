package com.felipefreitas.FourBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FourBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(FourBankApplication.class, args);
	}

}
