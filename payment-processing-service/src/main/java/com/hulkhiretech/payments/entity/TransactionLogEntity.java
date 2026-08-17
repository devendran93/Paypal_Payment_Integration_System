package com.hulkhiretech.payments.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLogEntity {

    private Integer id;
    private Integer transactionId;
    private String txnFromStatus;
    private String txnToStatus;
    private LocalDateTime creationDate;
}