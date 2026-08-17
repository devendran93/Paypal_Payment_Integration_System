package com.hulkhiretech.payments.paypalprovider;

import lombok.Data;

@Data
public class PPCreateOrderReq {

	private String currency;
	
	private Double amount;
	
	private String returnUrl;
	
	private String cancelUrl;
	
}
