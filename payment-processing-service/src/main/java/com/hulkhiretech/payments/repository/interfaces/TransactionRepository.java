package com.hulkhiretech.payments.repository.interfaces;

import com.hulkhiretech.payments.entity.TransactionEntity;

/**
 * Repository interface for transaction persistence operations.
 */
public interface TransactionRepository {

    /**
     * Persist the provided TransactionEntity.
     * No business logic should be implemented here; implementation may delegate to a data layer.
     *
     * @param transactionEntity the transaction entity to save
     */
    void saveTransaction(TransactionEntity transactionEntity);

    TransactionEntity getReferencebyTxnReference(String txnReference);

	boolean updateTransactionDetailsById(TransactionEntity transactionEntity);
    
    
}
 