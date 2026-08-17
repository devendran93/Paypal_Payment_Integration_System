package com.hulkhiretech.payments.service.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HttpRequest {
	
	private HttpMethod httpMethod;
	
	private String url;
	
	private HttpHeaders headers;
	
	private Object body;
	
}
