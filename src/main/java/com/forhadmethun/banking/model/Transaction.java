package com.forhadmethun.banking.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private Long transactionId;

    private BigDecimal transactionAmount;

    private String description;

    private Timestamp transactionDateTime;

    @ManyToOne
    @JoinColumn(name = "accountNumber")
    private Account account;
}
