package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class PaypalCreateOrderReq{

	private String currency;
	
	private Double amount;
	
	private String returnUrl;
	
	private String cancelUrl;
	
}
