package com.hulkhiretech.payments.service.helper;



import java.util.Collections;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.exception.PaymentProviderException;
import com.hulkhiretech.payments.paypal.PayPalErrorResponse;
import com.hulkhiretech.payments.paypal.createorder.req.Amount;
import com.hulkhiretech.payments.paypal.createorder.req.CreateOrderRequest;
import com.hulkhiretech.payments.paypal.createorder.req.ExperienceContext;
import com.hulkhiretech.payments.paypal.createorder.req.PaymentSource;
import com.hulkhiretech.payments.paypal.createorder.req.Paypal;
import com.hulkhiretech.payments.paypal.createorder.req.PurchaseUnit;
import com.hulkhiretech.payments.paypal.createorder.res.PPOrderResponse;
import com.hulkhiretech.payments.pojo.PaypalCreateOrderReq;
import com.hulkhiretech.payments.service.UniqueIdGenerator;
import com.hulkhiretech.payments.service.constants.Constants;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.service.http.HttpRequest;
import com.hulkhiretech.payments.util.ErrorUtil;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {
	
	@Value("${paypal.createOrderURL}")
	private String createOrderURL;
	
	private final JsonUtil jsonUtil;
	
	private final UniqueIdGenerator uniqueIdGenerator;
	
	public HttpRequest prepareCreateOrderRequest(PaypalCreateOrderReq request, String accessToken) {
		HttpHeaders ourCustomHeaders = new HttpHeaders();
		ourCustomHeaders.setBearerAuth(accessToken);
		ourCustomHeaders.set(Constants.PAY_PAL_REQUEST_ID, uniqueIdGenerator.getUniqueReqId());
		ourCustomHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		String reqJson = prepareRequestBody(request);
		
		HttpRequest httpRequest= HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url(createOrderURL)
				.headers(ourCustomHeaders) 
				.body(reqJson)
				.build();	
		
		return httpRequest;
	}

	private String prepareRequestBody(PaypalCreateOrderReq request) {
		
		ExperienceContext context = new ExperienceContext();
        context.setPaymentMethodPreference(Constants.IMMEDIATE_PAYMENT_REQUIRED);
        context.setLandingPage(Constants.LOGIN);
        context.setShippingPreference(Constants.NO_SHIPPING);
        context.setUserAction(Constants.PAY_NOW);
        context.setReturnUrl(request.getReturnUrl());
        context.setCancelUrl(request.getCancelUrl());

        Paypal paypal = new Paypal();
        paypal.setExperienceContext(context);

        PaymentSource paymentSource = new PaymentSource();
        paymentSource.setPaypal(paypal);

        Amount amount = new Amount();
        amount.setCurrencyCode(request.getCurrency());
        amount.setValue(String.valueOf(request.getAmount()));

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        CreateOrderRequest request1 = new CreateOrderRequest();
        request1.setIntent(Constants.CAPTURE);
        request1.setPaymentSource(paymentSource);
        request1.setPurchaseUnits(Collections.singletonList(unit));

        
		String reqJson = jsonUtil.toJson(request1);
		log.info("Converting String to JSON",reqJson);
		
		return reqJson ;
		
	}
	
	public PPOrderResponse processHttpResponse(ResponseEntity<String> httpresponse) {
		
		log.info("Processing HTTP response from PayPal create order API: Status code: {}, Body: {}", 
				httpresponse.getStatusCode(), httpresponse.getBody());
		
		if (httpresponse.getStatusCode().is2xxSuccessful()) {
			
			PPOrderResponse responseObj = jsonUtil.fromJson(httpresponse.getBody(), PPOrderResponse.class);		
			log.info("Parsed PayPal create order response into PPOrderResponse object: {}", responseObj);
			
			if(responseObj.getId() != null && 
					responseObj.getStatus() != null && 
					responseObj.getStatus().equals("PAYER_ACTION_REQUIRED")) {
				
				log.info("Order created successfully with ID: {} and status: {}", 
						responseObj.getId(), 
						responseObj.getStatus());
				
				return responseObj;
			}
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
				
			throw new PaymentProviderException(
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
			);
		}
		
		log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
				httpresponse.getStatusCode(), httpresponse.getBody());
		
		// if 4xx or 5xx
		if (httpresponse.getStatusCode().is4xxClientError() || httpresponse.getStatusCode().is5xxServerError()) {
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
			
			PayPalErrorResponse  errorResponse = jsonUtil.fromJson(httpresponse.getBody(), 
					PayPalErrorResponse .class);		
			log.info("Parsed PayPal create order response into PPOrderResponse object: {}", errorResponse);
					
			if(errorResponse.getName() != null || errorResponse.getError() != null) {
			
				String dynamicErrorMessage = ErrorUtil.generateErrorMessage(errorResponse); ;
				throw new PaymentProviderException(
						ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
						dynamicErrorMessage  ,
						HttpStatus.valueOf(httpresponse.getStatusCode().value())
						);				
			}
			
			log.error("Failed to create order with PayPal. Status code: {}, Body: {}", 
					httpresponse.getStatusCode(), httpresponse.getBody());
			
		}
		
		// if 1xx or 3xx		
			throw new PaymentProviderException(
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
			);
	}
	
}
