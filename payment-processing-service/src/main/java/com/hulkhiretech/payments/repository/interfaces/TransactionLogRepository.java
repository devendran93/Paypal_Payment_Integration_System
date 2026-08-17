package com.hulkhiretech.payments.repository.interfaces;

import com.hulkhiretech.payments.entity.TransactionLogEntity;

public interface TransactionLogRepository {

    boolean logStatusChange(TransactionLogEntity transactionLogEntity);

}