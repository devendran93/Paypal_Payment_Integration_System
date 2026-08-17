package com.hulkhiretech.payments.paypalprovider;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PPOrderRes {

	private String orderId;

	private String paypalStatus;

	private String redirectUrl;

	
}
