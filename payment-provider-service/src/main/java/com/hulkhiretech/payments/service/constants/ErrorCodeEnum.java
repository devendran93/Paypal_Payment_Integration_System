package com.hulkhiretech.payments.service.constants;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("30000", "Generic error. Please try again later."),
	UNABLE_TO_CONNECT_TO_PAYPAL("30001", "Unable to connect to PayPal. Please try again later."), 
	INVALID_PAYPAL_RESPONSE("30002", "Invalid response received from PayPal. Please try again later."), 
	PAYPAL_ERROR("30003","<Dynamic Message handling>");
	
	
	private final String errorCode;
	
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	
}
