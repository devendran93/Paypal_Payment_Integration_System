package com.hulkhiretech.payments.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.paypal.OAuthResponseJson;
import com.hulkhiretech.payments.service.constants.Constants;
import com.hulkhiretech.payments.service.http.HttpRequest;
import com.hulkhiretech.payments.service.http.HttpServiceEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

	private final HttpServiceEngine httpServiceEngine;
	
	private final ObjectMapper mapper;
	
	private static String accessToken;
	
	@Value("${paypal.clientId}")
	private String clientId;
	
	@Value("${paypal.clientSecretKey}")
	private String clientSecretKey;
	
	@Value("${paypal.oauthURL}")
	private String oauthUrl;

	public String getAccessToken() {
		if (accessToken != null) {
			log.info("Using cached access token.");
			return accessToken;
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId,clientSecretKey);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add(Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIALS);
		
		HttpRequest httpRequest= HttpRequest.builder()
			.httpMethod(HttpMethod.POST)
			.url(oauthUrl)
			.headers(headers)
			.body(body)
			.build();
		
		log.info("Generating access token...");
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpServiceEngine response: {}", httpResponse);
		
		try {
			OAuthResponseJson respone = mapper.readValue(httpResponse.getBody(), OAuthResponseJson.class);
			return respone.getAccessToken();
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse HTTP response: " + e.getMessage(), e);
		}
	}

}
