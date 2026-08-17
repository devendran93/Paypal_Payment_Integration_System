package com.hulkhiretech.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for the "Created" transaction status.
 * No business logic is implemented; method logs and returns a string.
 */
@Service
@Slf4j
public class CreatedStatusProcessor extends AbstractTxnStatusProcessor {
	

	public CreatedStatusProcessor(
			ModelMapper modelMapper, 
			TransactionRepository transactionRepository,
			TransactionLogRepository transactionLogRepository) {
		super(modelMapper,transactionRepository, transactionLogRepository);
	}
	
    @Override
    public TransactionDTO processInternal(TransactionDTO transactionDTO) {
        log.info("CreatedStatusProcessor process c alled with transactionDTO: {}", transactionDTO);
        
        TransactionEntity entity = convertToEntity(transactionDTO);
        log.info("CreatedStatusProcessor converted TransactionDTO to TransactionEntity: {}", entity);
        
        transactionRepository.saveTransaction(entity);
        
        log.info("CreatedStatusProcessor called saveTransaction on repository with entity: {}", entity.getId());
        
        log.info("CreatedStatusProcessor saved TransactionEntity to repository: {}", entity.getId());
        transactionDTO.setId(entity.getId());
        
        return transactionDTO;
    }
    
    private TransactionEntity convertToEntity(TransactionDTO transactionDTO) {
       return modelMapper.map(transactionDTO, TransactionEntity.class);
    }

}

