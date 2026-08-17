package com.hulkhiretech.payments.pojo;

import lombok.Data;

@Data
public class PaymentResponse {

	private int txnStatusId;
	
	private String txnReference;
}
