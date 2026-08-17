package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.dto.TransactionDTO;

/**
 * Interface for transaction status processors.
 */
public interface TxnStatusProcessor {

    /**
     * Process a transaction status.
     * No business logic is implemented here; implementations should log and return a string.
     *
     * @param Status the transaction status
     * @return a processed result string
     */
    TransactionDTO processStatus(TransactionDTO transactionDTO);
}
