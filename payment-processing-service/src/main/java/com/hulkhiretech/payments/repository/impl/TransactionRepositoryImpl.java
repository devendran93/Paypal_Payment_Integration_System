package com.hulkhiretech.payments.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.repository.interfaces.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple implementation of {@link TransactionRepository}.
 * The saveTransaction method intentionally contains no business logic as requested.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void saveTransaction(TransactionEntity transactionEntity) {

		String sql = """
				    INSERT INTO Transaction (
				        userId,
				        paymentMethodId,
				        providerId,
				        paymentTypeId,
				        txnStatusId,
				        amount,
				        currency,
				        merchantTransactionReference,
				        txnReference,
				        providerReference,
				        errorCode,
				        errorMessage,
				        retryCount
				    )
				    VALUES (
				        :userId,
				        :paymentMethodId,
				        :providerId,
				        :paymentTypeId,
				        :txnStatusId,
				        :amount,
				        :currency,
				        :merchantTransactionReference,
				        :txnReference,
				        :providerReference,
				        :errorCode,
				        :errorMessage,
				        :retryCount
				    )
				""";


		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(transactionEntity);

		KeyHolder keyHolder =
				new GeneratedKeyHolder();

		jdbcTemplate.update(
				sql,
				params,
				keyHolder,
				new String[]{ "id" }
				);

		if(keyHolder.getKey() != null) {
			log.info("Generated key for TransactionEntity: {}", keyHolder.getKey().intValue());
			transactionEntity.setId(keyHolder.getKey().intValue());
		}

	}

	@Override
	public TransactionEntity getReferencebyTxnReference(
			String txnReference) {

		log.info("Fetching TransactionEntity with txnReference: {}", txnReference);

		String sql = """
				    SELECT
				id,
				     	userId,
				        paymentMethodId,
				        providerId,
				        paymentTypeId,
				        txnStatusId,
				        amount,
				        currency,
				        merchantTransactionReference,
				        txnReference,
				        providerReference,
				        errorCode,
				        errorMessage,
				        retryCount
				    FROM payments.`Transaction`
				    WHERE txnReference = :txnReference
				    """;

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("txnReference",txnReference);

		try {
			TransactionEntity queryForObject = jdbcTemplate.queryForObject(
					sql,
					params,
					new BeanPropertyRowMapper<>(TransactionEntity.class)
					);

			log.info("Fetched TransactionEntity: {}", queryForObject);	   
			return queryForObject;

		} catch (Exception e) {			
			log.error("Error fetching transaction by reference = {} : {}", 
					txnReference, e.getMessage());
			
			return null;
			
		}

	}

	@Override
	public boolean updateTransactionDetailsById(TransactionEntity transactionEntity) {
		log.info("Updating TransactionEntity with id: {} and new txnStatusId: {}", 
				transactionEntity.getId(), transactionEntity.getTxnStatusId());

		String sql = """
				UPDATE Transaction
				SET txnStatusId = :txnStatusId,
					providerReference = :providerReference,
					errorCode = :errorCode,
					errorMessage = :errorMessage	            	
				WHERE id = :id
				""";

		BeanPropertySqlParameterSource params =
				new BeanPropertySqlParameterSource(transactionEntity);

		int rows = jdbcTemplate.update(sql, params);
		log.info("Updated TransactionEntity with id: {}. Rows affected: {}", 
				transactionEntity.getId(), rows);

		return rows > 0;
	}

}
