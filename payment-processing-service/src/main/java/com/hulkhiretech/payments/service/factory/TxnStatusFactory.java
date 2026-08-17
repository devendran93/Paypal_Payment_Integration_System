package com.hulkhiretech.payments.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payments.service.impl.processor.ApprovedStatusProcessor;
import com.hulkhiretech.payments.service.impl.processor.CreatedStatusProcessor;
import com.hulkhiretech.payments.service.impl.processor.FailureStatusProcessor;
import com.hulkhiretech.payments.service.impl.processor.InitiatedStatusProcessor;
import com.hulkhiretech.payments.service.impl.processor.PendingStatusProcessor;
import com.hulkhiretech.payments.service.impl.processor.SuccessStatusProcessor;
import com.hulkhiretech.payments.service.interfaces.TxnStatusProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory for obtaining transaction status processors.
 *
 * This factory uses the Spring ApplicationContext to retrieve processor beans
 * and selects the appropriate implementation based on the provided status.
 */
@Component
@Slf4j
public class TxnStatusFactory {

	@Autowired
    private ApplicationContext applicationContext;

    /**
     * Return a processor for the given transaction status.
     * Supported statuses (case-insensitive): CREATED, APPROVED, INITIATED,
     * PENDING, SUCCESS, FAILURE
     *
     * @param Status the transaction status
     * @return a TxnStatusProcessor instance, or null if no matching processor
     */
    public TxnStatusProcessor getProcessorStatus(int txnStatusId) {
        log.info("TxnStatusFactory.getProcessorStatus called with txnStatusId: {}", txnStatusId);

        int key = txnStatusId;

        switch (key) {
            case 1: // CREATED
                return applicationContext.getBean(CreatedStatusProcessor.class);
            case 2: // INITIATED
                return applicationContext.getBean(InitiatedStatusProcessor.class);
            case 3: // PENDING
                return applicationContext.getBean(PendingStatusProcessor.class);
            case 4: // APPROVED
                return applicationContext.getBean(ApprovedStatusProcessor.class);
            case 5: // SUCCESS
                return applicationContext.getBean(SuccessStatusProcessor.class);
            case 6: // FAILURE
                return applicationContext.getBean(FailureStatusProcessor.class);
            default:
                log.warn("No processor found for txnStatusId: {}", txnStatusId);
                throw new RuntimeException("No processor found for txnStatusId: " + txnStatusId);
        }
    }
}
