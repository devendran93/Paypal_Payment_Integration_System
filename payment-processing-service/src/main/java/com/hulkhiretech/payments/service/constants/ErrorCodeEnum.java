package com.hulkhiretech.payments.service.constants;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("20000", "Generic error. Please try again later."),
	UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE("20001", "Unable to connect to external service. Please try again later."),
	INVALID_PAYPAL_PROVIDER_RESPONSE("20002", "Invalid response from PayPal provider."), 
	ERROR_PROCESSING_CREATE_ORDER("20003","Error Processing create order request."), 
	TRANSACTION_UPDATE_FAILED("20004","Failed to update transaction status."),
	DUPLICATE_STATUS_UPDATE("20005","Duplicate status update, transaction already has the same status."),
	FINAL_STATUS_UPDATE_NOT_ALLOWED("20006","Transaction has in final status, no need to update.");
	
	
	private final String errorCode;
	
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	
}
