package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreatePaymentReq;
import com.hulkhiretech.payments.pojo.InitiatePaymentReq;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {

    /**
     * Create a payment.
     * @return a simple status string (no business logic)
     */
    PaymentResponse createPayment(CreatePaymentReq createPaymentReq);

    /**
     * Initiate a payment.
     * @return a simple status string (no business logic)
     */
    String initiatePayment(String txnReference, InitiatePaymentReq initiatePaymentReq);

    /**
     * Capture a payment.
     * @return a simple status string (no business logic)
     */
    String capturePayment();
}
