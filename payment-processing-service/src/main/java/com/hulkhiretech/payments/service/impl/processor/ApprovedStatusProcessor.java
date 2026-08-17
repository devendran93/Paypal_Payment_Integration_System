package com.hulkhiretech.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for the "Approved" transaction status.
 * No business logic is implemented; method logs and returns a string.
 */
@Service
@Slf4j
public class ApprovedStatusProcessor extends AbstractTxnStatusProcessor {

	public ApprovedStatusProcessor(
			ModelMapper modelMapper, 
			TransactionRepository transactionRepository,
			TransactionLogRepository transactionLogRepository) {
		super(modelMapper,transactionRepository, transactionLogRepository);
	}
	
    @Override
    public TransactionDTO processInternal(TransactionDTO transactionDTO) {
        log.info("ApprovedStatusProcessor.process called");
        return transactionDTO;
    }
}
