package com.hulkhiretech.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.exception.PaymentProcessingException;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for the "Pending" transaction status.
 * No business logic is implemented; method logs and returns a string.
 */

@Service
@Slf4j
public class PendingStatusProcessor extends AbstractTxnStatusProcessor {
	
	public PendingStatusProcessor(
			ModelMapper modelMapper, 
			TransactionRepository transactionRepository,
			TransactionLogRepository transactionLogRepository) {
		super(modelMapper,transactionRepository, transactionLogRepository);
	}

    @Override
    public TransactionDTO processInternal(TransactionDTO transactionDTO) {

        log.info("PendingStatusProcessor.process called with transactionDTO: {}", transactionDTO);
        
                
        TransactionEntity entity = modelMapper.map(transactionDTO, TransactionEntity.class);
        log.info("PendingStatusProcessor converted TransactionDTO to TransactionEntity: {}", transactionDTO);
        
        boolean isUpdated = transactionRepository.updateTransactionDetailsById(entity);
        log.info("PendingStatusProcessor called updateTransactionDetailsById on repository with entity: {} and txnStatus: {}", 
        		entity.getId(), entity.getTxnStatusId());
        
        if(!isUpdated) {
			log.warn("PendingStatusProcessor failed to update TransactionEntity in repository: {}", entity.getId());
			throw new PaymentProcessingException(
						ErrorCodeEnum.TRANSACTION_UPDATE_FAILED.getErrorCode(),
						ErrorCodeEnum.TRANSACTION_UPDATE_FAILED.getErrorMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
        
        
        log.info("PendingStatusProcessor successfully updated TransactionEntity in repository: {}", entity.getId());
        return transactionDTO;
    }
}
