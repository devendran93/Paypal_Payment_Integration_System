package com.hulkhiretech.payments.util;

import java.util.function.Consumer;

import com.hulkhiretech.payments.paypal.PayPalErrorDetail;
import com.hulkhiretech.payments.paypal.PayPalErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorUtil {
	
	public static String generateErrorMessage(PayPalErrorResponse response) {
		log.info("Generating error message from PayPalErrorResponse: {}", response);
		
	    if (response == null) {
	    	log.warn("PayPalErrorResponse is null, returning empty error message.");
	        return "";
	    }

	    StringBuilder sb = new StringBuilder();

	    Consumer<String> append = value -> {
	        if (value != null && !value.trim().isEmpty()) {
	            if (sb.length() > 0) {
	                sb.append(" | ");
	            }
	            sb.append(value);
	        }
	    };

	    append.accept(response.getName());
	    append.accept(response.getMessage());
	    append.accept(response.getError());
	    append.accept(response.getErrorDescription());

	    if (response.getDetails() != null && !response.getDetails().isEmpty()) {
	        PayPalErrorDetail detail = response.getDetails().get(0);

	        if (detail != null) {
	            append.accept(detail.getField());
	            append.accept(detail.getIssue());
	            append.accept(detail.getDescription());
	        }
	    }
	    
	    log.info("Generated error message: {}", sb.toString());
	    return sb.toString();
	}
	
}
