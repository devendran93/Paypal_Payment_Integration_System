package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class InitiatePaymentReq {

	private String returnUrl;
	
	private String cancelUrl;
	
	
}
