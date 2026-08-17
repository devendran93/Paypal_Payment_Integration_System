package com.hulkhiretech.payments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaypalProviderServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(PaypalProviderServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting PaypalProviderServiceApplication...");
		SpringApplication.run(PaypalProviderServiceApplication.class, args);
		logger.info("PaypalProviderServiceApplication started.");
	}

}
