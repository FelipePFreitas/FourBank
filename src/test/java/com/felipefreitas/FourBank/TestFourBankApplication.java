package com.felipefreitas.FourBank;

import org.springframework.boot.SpringApplication;

public class TestFourBankApplication {

	public static void main(String[] args) {
		SpringApplication.from(FourBankApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
