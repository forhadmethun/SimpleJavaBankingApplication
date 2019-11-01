package com.forhadmethun.banking.dto.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class TransactionDto {

    private Long transactionId;

    private String description;

    private BigDecimal transactionAmount;

    private Timestamp transactionDateTime;
}
