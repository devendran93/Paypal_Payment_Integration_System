package com.hulkhiretech.payments.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.hulkhiretech.payments.paypal.createorder.res.Link;
import com.hulkhiretech.payments.paypal.createorder.res.PPOrderResponse;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.pojo.PaypalCreateOrderReq;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.http.HttpRequest;
import com.hulkhiretech.payments.service.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final HttpServiceEngine httpClientService;

	private final TokenService tokenService;
	
	private final CreateOrderHelper createOrderHelper;
	
	private final CaptureOrderHelper captureOrderHelper;

	@Override
	public OrderRes createOrder(@RequestBody PaypalCreateOrderReq request) {
		log.info("Creating order with CreateOrderRequest: {}", request);
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token obtained: {}", accessToken);
		
		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderRequest(request, accessToken);
		log.info("Prepared HttpRequest for PayPal create order API: {}", httpRequest);
		
		ResponseEntity<String> httpresponse = httpClientService.makeHttpCall(httpRequest);
		log.info("Received response from PayPal create order API: {}", httpresponse);
		
		PPOrderResponse responseObj = createOrderHelper.processHttpResponse(httpresponse);
		log.info("Parsed PayPal create order response into PPOrderResponse object: {}", responseObj);
		
		OrderRes orderResponse = new OrderRes();
		orderResponse.setOrderId(responseObj.getId());
		orderResponse.setPaypalStatus(responseObj.getStatus());
		
		List<Link> links = responseObj.getLinks();
		String payerActionLink = links.stream()
				.filter(link -> "payer-action".equals(link.getRel()))
				.map(Link::getHref)
				.findFirst()
				.orElse(null);
		
		orderResponse.setRedirectUrl(payerActionLink);
		
		log.info("Constructed OrderRes object to return: {}", orderResponse);
		return orderResponse;
	}


	@Override
	public OrderRes captureOrder(String orderId) {
		log.info("Capturing order with Order ID: {}", orderId);
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token obtained: {}", accessToken);
		
		HttpRequest httpRequest = captureOrderHelper.prepareCaptureOrderRequest(orderId, accessToken);
		log.info("Prepared HttpRequest for PayPal capture order API: {}", httpRequest);
		
		ResponseEntity<String> httpResponse = httpClientService.makeHttpCall(httpRequest);
		log.info("Received response from PayPal capture order API: {}", httpResponse);
		
		PPOrderResponse responseObj = captureOrderHelper.processHttpResponse(httpResponse);
		log.info("Parsed PayPal capture order response into PPOrderResponse object: {}", responseObj);
		
		OrderRes orderResponse = new OrderRes();
		orderResponse.setOrderId(responseObj.getId());
		orderResponse.setPaypalStatus(responseObj.getStatus());
		
		List<Link> links = responseObj.getLinks();
		String payerActionLink = links.stream()
				.filter(link -> "payer-action".equals(link.getRel()))
				.map(Link::getHref)
				.findFirst()
				.orElse(null);
		
		orderResponse.setRedirectUrl(payerActionLink);
		
		log.info("Constructed OrderRes object to return: {}", orderResponse);		
		return orderResponse;
	}

}




