package com.hulkhiretech.payments.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentReq {

    private int userId;

    private int paymentMethodId;

    private int providerId;

    private int paymentTypeId;

    private BigDecimal amount = BigDecimal.ZERO;

    private String currency;

    private String merchantTransactionReference;
}