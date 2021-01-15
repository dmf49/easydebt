package com.dsm.test.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.dsm.test.example.service.DebtPaymentService;

@SpringBootApplication
public class DebtApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DebtApplication.class, args);
	}

	@Autowired 
	DebtPaymentService debtPaymentService;
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("---- Debt Services ----------");
		System.out.println(debtPaymentService.execute());
	}

}
