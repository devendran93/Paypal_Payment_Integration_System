package com.hulkhiretech.payments.service.impl.processor;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import com.hulkhiretech.payments.dto.TransactionDTO;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.entity.TransactionLogEntity;
import com.hulkhiretech.payments.exception.PaymentProcessingException;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payments.service.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.service.constants.TransactionStatusEnum;
import com.hulkhiretech.payments.service.interfaces.TxnStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTxnStatusProcessor implements TxnStatusProcessor {

	protected ModelMapper modelMapper;

	protected TransactionRepository transactionRepository;

	private TransactionLogRepository transactionLogRepository;

	public AbstractTxnStatusProcessor(
			ModelMapper modelMapper,
			TransactionRepository transactionRepository,
			TransactionLogRepository transactionLogRepository) {
		this.modelMapper = modelMapper;
		this.transactionRepository = transactionRepository;
		this.transactionLogRepository = transactionLogRepository;
	}

	@Override
	public TransactionDTO processStatus(TransactionDTO transactionDTO) {
		log.info("{}, invoked with transactionDTO = {}", 
				this.getClass().getSimpleName(), transactionDTO);


		TransactionEntity oldEntity = transactionRepository.getReferencebyTxnReference(
				transactionDTO.getTxnReference());

		log.info("Fetching exixting TransactionEntity={}", oldEntity);


		//STEP - 1 
		//Old Entity status is already same means throw an error
		DuplicateTxnCheck(transactionDTO, oldEntity);

		//STEP - 2 
		//Old Entity status is success or failure means throw a error
		finalStatusCheck(transactionDTO, oldEntity);

		//STEP - 3 
		//Called processInternal to do status specific processing 
		TransactionDTO txnDto =  processInternal(transactionDTO);
		log.info("Completed processing with result:{}", txnDto);

		//STEP - 4 
		//insert into TransactionLogEntity
		logTxnStatus(oldEntity, txnDto);

		//STEP - 5 
		//For success / failure status raise event to Kafka
		sendKafkaEventForFinalStatus(txnDto);

		return txnDto;
	}

	private void sendKafkaEventForFinalStatus(TransactionDTO txnDto) {
		if (txnDto.getTxnStatusId() == TransactionStatusEnum.SUCCESS.getId()
				|| txnDto.getTxnStatusId() == TransactionStatusEnum.FAILED.getId()) {

			
			log.info("Raising event to kafka for transaction reference {} with status ID {}",
					txnDto.getTxnReference(), txnDto.getTxnStatusId());
			
		}
	}

	private void logTxnStatus(TransactionEntity oldEntity, TransactionDTO txnDto) {
		String oldStatus = oldEntity != null 
				?  TransactionStatusEnum.getById(oldEntity.getTxnStatusId()).getName()
						:  "N/A";

		TransactionLogEntity txnLog = TransactionLogEntity.builder()
				.transactionId(txnDto.getId())
				.txnFromStatus(oldStatus)
				.txnToStatus(TransactionStatusEnum
						.getById(txnDto.getTxnStatusId())
						.getName())
				.build();

		transactionLogRepository.logStatusChange(txnLog);

		log.info("completed processing with result - {}", txnDto);
	}

	private void finalStatusCheck(TransactionDTO transactionDTO, TransactionEntity oldEntity) {
		if(oldEntity != null &&
				(oldEntity.getTxnStatusId() == TransactionStatusEnum.SUCCESS.getId() 
				|| oldEntity.getTxnStatusId() == TransactionStatusEnum.FAILED.getId())) {

			log.error("Transaction with reference {} has already final status ID {}. No further process",
					transactionDTO.getTxnReference(), transactionDTO.getTxnStatusId());

			throw new PaymentProcessingException(
					ErrorCodeEnum.FINAL_STATUS_UPDATE_NOT_ALLOWED.getErrorCode(), 
					ErrorCodeEnum.FINAL_STATUS_UPDATE_NOT_ALLOWED.getErrorMessage(), 
					HttpStatus.INTERNAL_SERVER_ERROR
					);	

		}
	}

	private void DuplicateTxnCheck(TransactionDTO transactionDTO, TransactionEntity oldEntity) {
		if(oldEntity != null &&
				oldEntity.getTxnStatusId() == transactionDTO.getTxnStatusId()) {

			log.error("Transaction with reference {} has already status ID {}. No processing need",
					transactionDTO.getTxnReference(), transactionDTO.getTxnStatusId());

			throw new PaymentProcessingException(
					ErrorCodeEnum.DUPLICATE_STATUS_UPDATE.getErrorCode(), 
					ErrorCodeEnum.DUPLICATE_STATUS_UPDATE.getErrorMessage(), 
					HttpStatus.INTERNAL_SERVER_ERROR
					);			
		}
	}

	public abstract TransactionDTO processInternal(TransactionDTO transactionDTO);
}
