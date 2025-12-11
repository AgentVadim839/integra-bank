package com.bank.integra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class IntegraApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegraApplication.class, args);
	}

}
