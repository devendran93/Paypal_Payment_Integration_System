package com.hulkhiretech.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.InitiatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentReq createPaymentReq) {
        
    	log.info("createPayment called with request: {}", 
    			createPaymentReq);
        
    	PaymentResponse paymentResponse = paymentService.createPayment(
    			createPaymentReq);
    	
        log.info("createPayment response: {}", 
        		paymentResponse);
        
        return ResponseEntity.ok(paymentResponse);
        
    }

    @PostMapping("/{txnReference}/initiate")
    public String initiatePayment(@PathVariable String txnReference,@RequestBody InitiatePaymentReq initiatePaymentReq ) {
    	log.info("intiatePayment called txnReference: {} + initiatePaymentReq: {}", txnReference, initiatePaymentReq);

        String response = paymentService.initiatePayment(txnReference,initiatePaymentReq);
        
        log.info("intiatePayment response: {}", response);
        return response;
    }

    @PostMapping("/{captureId}/capture")
    public String capturePayment(@PathVariable String captureId) {
        log.info("capturePayment called");
        String response = paymentService.capturePayment();
        log.info("capturePayment response: {}", response);
        return response;
    }

}
