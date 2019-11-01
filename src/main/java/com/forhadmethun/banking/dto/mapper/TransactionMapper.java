package com.forhadmethun.banking.dto.mapper;

import com.forhadmethun.banking.dto.model.TransactionDto;
import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.model.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {
    public static TransactionDto toTransactionDto(Transaction transaction){
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .debitAccountNumber(transaction.getDebitAccountNumber())
                .creditAccountNumber(transaction.getCreditAccountNumber())
                .transactionAmount(transaction.getTransactionAmount())
                .transactionDateTime(transaction.getTransactionDateTime())
                .build();
    }

    public static List<TransactionDto> toTransactionDtoList(List<Transaction> transactionList){
        return transactionList.stream()
                .map(transaction -> toTransactionDto(transaction))
                .collect(Collectors.toList());
    }

}
