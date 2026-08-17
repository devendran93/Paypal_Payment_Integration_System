package com.hulkhiretech.payments.service.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.exception.PaymentProviderException;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {
	
	private final RestClient restClient;
    /**
     * Placeholder for making HTTP calls. Intentionally contains no logic.
     * Implementations should be provided later.
     */
	
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
	
	log.info("Making HTTP call to URL: {}", httpRequest.getUrl());
	
	try {	
		ResponseEntity<String> httpResponse = restClient.method(httpRequest.getHttpMethod())
		.uri(httpRequest.getUrl())
		.headers(headers -> headers.addAll(httpRequest.getHeaders()))
		.body(httpRequest.getBody())
		.retrieve()
		.toEntity(String.class);
		
		return httpResponse;
		
	}catch (HttpClientErrorException | HttpServerErrorException e) {
		log.error("HTTP error in HttpServiceEngine.makeHttpCall(): {}", e.getStatusCode());
		
		// if server return 503 or 504, throw PaymentProviderException
		if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
			log.error("Server returned 503 or 504, throwing PaymentProviderException");
			throw new PaymentProviderException(
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL.getErrorCode(),
					ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL.getErrorMessage(),
					HttpStatus.valueOf(e.getStatusCode().value()));
		}
		
		ResponseEntity<String> errorResponse = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
		log.info("Returning error response entity: {}", errorResponse);
		return errorResponse;
		
	}	
	catch (Exception e) {
		 log.error("Error in HttpServiceEngine.makeHttpCall(): {}", e);
		 throw new PaymentProviderException(
				 ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL.getErrorCode(),
				 ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL.getErrorMessage(),
				 HttpStatus.INTERNAL_SERVER_ERROR);
	}
  }
}
