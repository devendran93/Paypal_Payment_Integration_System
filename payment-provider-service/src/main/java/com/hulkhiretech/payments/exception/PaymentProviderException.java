package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentProviderException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
    
    public PaymentProviderException(
    		String errorCode, 
    		String errorMessage, 
    		HttpStatus httpStatus) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
	}

}