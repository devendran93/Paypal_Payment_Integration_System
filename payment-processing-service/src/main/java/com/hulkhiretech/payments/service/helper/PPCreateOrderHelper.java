package com.hulkhiretech.payments.service.helper;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.exception.PaymentProcessingException;
import com.hulkhiretech.payments.paypalprovider.PPCreateOrderReq;
import com.hulkhiretech.payments.paypalprovider.PPErrorResponse;
import com.hulkhiretech.payments.paypalprovider.PPOrderRes;
import com.hulkhiretech.payments.pojo.InitiatePaymentReq;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.service.http.HttpRequest;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PPCreateOrderHelper {
	
	private final JsonUtil jsonUtil;
	
	public HttpRequest prepareHttpRequest(TransactionDTO  transactionDTO, InitiatePaymentReq initiatePaymentReq) {
		
		HttpHeaders ourCustomeHeaders = new HttpHeaders();
		ourCustomeHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		String requestBodyJson = prepareRequestBody(transactionDTO, initiatePaymentReq);		
		
		HttpRequest httpRequest = HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url("http://localhost:8083/v1/orders")
				.headers(ourCustomeHeaders)
				.body(requestBodyJson)
				.build();
								
		return httpRequest;
		
	}

	private String prepareRequestBody(TransactionDTO transactionDTO, InitiatePaymentReq initiatePaymentReq) {

		
		
		PPCreateOrderReq ppCreateOrderReq = new PPCreateOrderReq();
		ppCreateOrderReq.setAmount(transactionDTO.getAmount().doubleValue());
		ppCreateOrderReq.setCurrency(transactionDTO.getCurrency());
		ppCreateOrderReq.setReturnUrl(initiatePaymentReq.getReturnUrl());
		ppCreateOrderReq.setCancelUrl(initiatePaymentReq.getCancelUrl());
		
		String requestBodyJson = jsonUtil.toJson(ppCreateOrderReq);
		log.info("PPCreateOrderHelper.prepareRequestBody created requestBodyJson: {}", requestBodyJson);
				
		return requestBodyJson;
	}

	public PPOrderRes processHttpResponse(ResponseEntity<String> httpresponse) {
		
		log.info("Processing HTTP response from PayPal create order API: Status code: {}, Body: {}", 
				httpresponse.getStatusCode(), httpresponse.getBody());
		
		if (httpresponse.getStatusCode().is2xxSuccessful()) {
			
			PPOrderRes responseObj = jsonUtil.fromJson(httpresponse.getBody(), PPOrderRes.class);		
			log.info("Parsed PayPal create order response into PPOrderResponse object: {}", responseObj);
			
			if(responseObj.getOrderId() != null && 
					responseObj.getPaypalStatus() != null && 
					responseObj.getPaypalStatus().equals("PAYER_ACTION_REQUIRED")) {
				
				log.info("Order created successfully with ID: {} and status: {}", 
						responseObj.getOrderId(), 
						responseObj.getPaypalStatus());
				
				return responseObj;
			}
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
				
			throw new PaymentProcessingException(
				ErrorCodeEnum.INVALID_PAYPAL_PROVIDER_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_PAYPAL_PROVIDER_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY 
			);
		}
		
		log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
				httpresponse.getStatusCode(), httpresponse.getBody());
		
		// if 4xx or 5xx
		if (httpresponse.getStatusCode().is4xxClientError() || httpresponse.getStatusCode().is5xxServerError()) {
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
			
			PPErrorResponse errorResponse = jsonUtil.fromJson(httpresponse.getBody(), 
					PPErrorResponse .class);		
			log.info("Parsed PayPal create order response into PPOrderResponse object: {}", errorResponse);
					
			if(errorResponse.getErrorCode() != null || errorResponse.getErrorMessage() != null) {
				
				log.error("PayPal API returned an error. Error code: {}, Error message: {}", 
						errorResponse.getErrorCode(), errorResponse.getErrorMessage());
				
				throw new PaymentProcessingException(
						errorResponse.getErrorCode(),
						errorResponse.getErrorMessage(),
						HttpStatus.valueOf(httpresponse.getStatusCode().value())
						);				
			}
			
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
			
		}
		
		// if 1xx or 3xx		
			throw new PaymentProcessingException(
				ErrorCodeEnum.INVALID_PAYPAL_PROVIDER_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_PAYPAL_PROVIDER_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
			);
	}
	
	
}
