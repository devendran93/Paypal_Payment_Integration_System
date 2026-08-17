package com.hulkhiretech.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for the "Initiated" transaction status.
 * No business logic is implemented; method logs and returns a string.
 */
@Service
@Slf4j
public class InitiatedStatusProcessor extends AbstractTxnStatusProcessor {

	public InitiatedStatusProcessor(
			ModelMapper modelMapper, 
			TransactionRepository transactionRepository,
			TransactionLogRepository transactionLogRepository) {
		super(modelMapper,transactionRepository, transactionLogRepository);
	}
	
    @Override
    public TransactionDTO processInternal(TransactionDTO transactionDTO) {
        log.info("InitiatedStatusProcessor.process called with transactionDTO: {}", transactionDTO);
        
                
        TransactionEntity entity = modelMapper.map(transactionDTO, TransactionEntity.class);
        log.info("InitiatedStatusProcessor converted TransactionDTO to TransactionEntity: {}", transactionDTO);
        
        boolean isUpdated = transactionRepository.updateTransactionDetailsById(entity);
        log.info("InitiatedStatusProcessor called updateTransactionDetailsById on repository with entity: {} and txnStatus: {}", 
        		entity.getId(), entity.getTxnStatusId());
        
        if(!isUpdated) {
			log.warn("InitiatedStatusProcessor failed to update TransactionEntity in repository: {}", entity.getId());
			throw new RuntimeException("Failed to update TransactionEntity in repository for ID: " + entity.getId());
		}
        
        
        log.info("InitiatedStatusProcessor successfully updated TransactionEntity in repository: {}", entity.getId());
        return transactionDTO;
    }
}
