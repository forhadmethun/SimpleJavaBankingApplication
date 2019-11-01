package com.forhadmethun.banking.service;

import com.forhadmethun.banking.controller.request.TransferBalanceRequest;
import com.forhadmethun.banking.dto.model.AccountDto;
import com.forhadmethun.banking.dto.model.AccountStatement;
import com.forhadmethun.banking.dto.model.TransactionDto;
import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.exception.BankTransactionException;

import java.util.List;

public interface AccountService {

    List<AccountDto> findAll();

    Account findByAccountNumber(String accountNumber);

    AccountDto save(Account account)
            throws BankTransactionException;

    TransactionDto sendMoney(
            Account fromAccount,
            Account toAccount,
            TransferBalanceRequest transferBalanceRequest
    ) throws BankTransactionException;

    AccountStatement getStatement(String accountNumber)
            throws BankTransactionException;
}
