package com.hulkhiretech.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.pojo.PaypalCreateOrderReq;
import com.hulkhiretech.payments.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
	
    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderRes> createOrder(@RequestBody PaypalCreateOrderReq request) {
        log.info("Start createOrderEndpoint");
        
        OrderRes response = orderService.createOrder(request);
        log.info("End createOrderEndpoint, response: {}", response);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{orderId}/capture")
    public OrderRes captureOrder(@PathVariable String orderId) {
		log.info("Start captureOrderEndpoint");
		
		OrderRes response = orderService.captureOrder(orderId);
		log.info("End captureOrderEndpoint, response: {}", response);
		
		return response;
	}
    
}


