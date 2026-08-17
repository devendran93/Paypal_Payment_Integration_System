package com.hulkhiretech.payments.service;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.service.factory.TxnStatusFactory;
import com.hulkhiretech.payments.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {
	
	private final TxnStatusFactory txnStatusFactory;
	
    /**
     * Process payment status.
     * This method intentionally contains no business logic and simply returns a string.
     * @param transactionDTO 
     */
    public TransactionDTO processStatus(TransactionDTO transactionDTO) {
        log.info("processStatus called with status: {}", transactionDTO);
        
        TxnStatusProcessor processor = txnStatusFactory.getProcessorStatus(transactionDTO.getTxnStatusId());
        
        log.info("Processor obtained: {}", processor.getClass().getSimpleName());	
        
        TransactionDTO txnData = processor.processStatus(transactionDTO);
        
        log.info("Response from processor: {}", txnData);
		return txnData;
        
    }
}
