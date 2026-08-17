package com.hulkhiretech.payments.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.exception.PaymentProcessingException;
import com.hulkhiretech.payments.paypalprovider.PPOrderRes;
import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.InitiatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payments.service.PaymentStatusService;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.service.helper.PPCreateOrderHelper;
import com.hulkhiretech.payments.service.http.HttpRequest;
import com.hulkhiretech.payments.service.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
	private final PaymentStatusService paymentStatusService;

	private final ModelMapper modelMapper;
	
	private final TransactionRepository transactionRepository;
	
	private final PPCreateOrderHelper ppCreateOrderHelper;
	
	private final HttpServiceEngine httpServiceEngine;
	
    @Override
    public PaymentResponse createPayment(CreatePaymentReq createPaymentReq) {
        log.info("PaymentServiceImpl.createPayment called with createPaymentReq: {}", createPaymentReq);
        
        TransactionDTO transactionDTO = TransactionDTO.builder()
        		.userId(createPaymentReq.getUserId())
        		.paymentMethodId(createPaymentReq.getPaymentMethodId())
        		.providerId(createPaymentReq.getProviderId())
        		.paymentTypeId(createPaymentReq.getPaymentTypeId())
        		.amount(createPaymentReq.getAmount())
        		.currency(createPaymentReq.getCurrency())
        		.merchantTransactionReference(createPaymentReq.getMerchantTransactionReference())
        		.txnStatusId(1)
        		.txnReference(generateTxnReference())
				.build();
        
        log.info("PaymentServiceImpl.createPayment created TransactionDTO: {}", transactionDTO);
        
        TransactionDTO txnDTO = paymentStatusService.processStatus(transactionDTO);
        log.info("PaymentServiceImpl.createPayment response: {}", txnDTO);
        
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTxnStatusId(txnDTO.getTxnStatusId());
        paymentResponse.setTxnReference(txnDTO.getTxnReference());
        
        return paymentResponse;
        
        
    }

	private String generateTxnReference() {
		return UUID.randomUUID().toString();
	}

	@Override
    public String initiatePayment(String txnReference,@RequestBody InitiatePaymentReq initiatePaymentReq) {
        log.info("PaymentServiceImpl.initiatePayment called with txnReference: {} + initiatePaymentReq : {}", txnReference, initiatePaymentReq);
        
    	
    	TransactionEntity txnEntity = transactionRepository.getReferencebyTxnReference(txnReference);
    	log.info("TransactionEntity retrieved: {}", txnEntity);
    	
    	TransactionDTO transactionDTO = modelMapper.map(txnEntity, TransactionDTO.class);
    	
    	
    	transactionDTO.setTxnStatusId(2); // Set status to "Initiated"
    	transactionDTO = paymentStatusService.processStatus(transactionDTO);
    	log.info("TransactionDTO after processing status: {}", transactionDTO);
    	
    	HttpRequest httpRequest = ppCreateOrderHelper.prepareHttpRequest(transactionDTO, initiatePaymentReq);
    	log.info("HttpRequest prepared: {}", httpRequest);
    	
    	ResponseEntity<String> httpResponse = null;
    	
    	try{
    		httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
        	log.info("HTTP call response: {}", httpResponse);
        	
        	PPOrderRes response  = ppCreateOrderHelper.processHttpResponse(httpResponse);
        	log.info("Processed HTTP response into PPOrderRes: {}", response);
        	
        	transactionDTO.setTxnStatusId(3);
        	transactionDTO.setProviderReference(response.getOrderId());
        	
        	paymentStatusService.processStatus(transactionDTO);
        	
        	
    	} catch(PaymentProcessingException e) {
    		log.info("Error during HTTP call to PayPal: {}", e.getErrorMessage(), e);
    		
    		processFailedStatus(transactionDTO, e.getErrorCode(),e.getErrorMessage());
    		
    		throw e;
    		
    	} catch(Exception e) {
    		
    		log.info("Unexpected Error during HTTP call to PayPal", e);
    		
    		processFailedStatus(transactionDTO, ErrorCodeEnum.ERROR_PROCESSING_CREATE_ORDER.getErrorCode(), 
    				ErrorCodeEnum.ERROR_PROCESSING_CREATE_ORDER.getErrorMessage());
    		
    		throw new PaymentProcessingException(
    		
    				ErrorCodeEnum.ERROR_PROCESSING_CREATE_ORDER.getErrorCode(),
    				ErrorCodeEnum.ERROR_PROCESSING_CREATE_ORDER.getErrorMessage(),
    				HttpStatus.INTERNAL_SERVER_ERROR
    				
    		);
    	}
        
        return httpResponse.getBody();
    }

	    private void processFailedStatus(TransactionDTO transactionDTO, String errorCode, String errorMessage) {
	    	
	     log.info("Processing failed transaction. TransactionId: {}, ErrorCode: {}, ErrorMessage: {}",
	    	            transactionDTO.getId(), errorCode, errorMessage);
	    	
			transactionDTO.setTxnStatusId(6);
			transactionDTO.setErrorCode(errorCode);
			transactionDTO.setErrorMessage(errorMessage);
			
			paymentStatusService.processStatus(transactionDTO);
			
		}

	@Override
    public String capturePayment() {
        log.info("PaymentServiceImpl.capturePayment called");
        TransactionDTO transactionDTO = TransactionDTO.builder()
        		.txnStatusId(4)
				.build();
        
        TransactionDTO response = paymentStatusService.processStatus(transactionDTO);
        log.info("PaymentServiceImpl.capturePayment response: {}", response);
        return "";
    }
}
