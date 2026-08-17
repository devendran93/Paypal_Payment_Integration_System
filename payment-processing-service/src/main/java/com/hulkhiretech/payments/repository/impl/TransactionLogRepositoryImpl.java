package com.hulkhiretech.payments.repository.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.entity.TransactionLogEntity;
import com.hulkhiretech.payments.repository.interfaces.TransactionLogRepository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TransactionLogRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean logStatusChange(TransactionLogEntity transactionLogEntity) {

        log.info("Logging transaction status change : {}", transactionLogEntity);

        String sql = """
                INSERT INTO Transaction_Log
                (
                    transactionId,
                    txnFromStatus,
                    txnToStatus
                )
                VALUES
                (
                    :transactionId,
                    :txnFromStatus,
                    :txnToStatus
                )
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("transactionId", transactionLogEntity.getTransactionId())
                .addValue("txnFromStatus", transactionLogEntity.getTxnFromStatus())
                .addValue("txnToStatus", transactionLogEntity.getTxnToStatus());

        int rows = jdbcTemplate.update(sql, params);

        log.info("Transaction log inserted successfully. Rows affected={}", rows);

        return rows > 0;
    }
}