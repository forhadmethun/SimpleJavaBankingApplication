package com.forhadmethun.banking.service.impl;

import com.forhadmethun.banking.controller.request.TransferBalanceRequest;
import com.forhadmethun.banking.dto.mapper.AccountMapper;
import com.forhadmethun.banking.dto.mapper.TransactionMapper;
import com.forhadmethun.banking.dto.model.AccountDto;
import com.forhadmethun.banking.dto.model.AccountStatement;
import com.forhadmethun.banking.dto.model.TransactionDto;

import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.model.Transaction;
import com.forhadmethun.banking.exception.BankTransactionException;
import com.forhadmethun.banking.repository.AccountRepository;
import com.forhadmethun.banking.repository.TransactionRepository;
import com.forhadmethun.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public AccountDto save(Account account)
            throws BankTransactionException {
        Account existingAccount = accountRepository.findByAccountNumberEquals(
                account.getAccountNumber()
        );
        if (existingAccount != null) {
            throw new BankTransactionException("Account already exists.");
        }

        accountRepository.save(account);
        return AccountMapper.toAccountDto(
                accountRepository.findByAccountNumberEquals(account.getAccountNumber()
                )
        );
    }

    public List<AccountDto> findAll() {
        return AccountMapper.toAccountDtoList(
                accountRepository.findAll()
        );
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumberEquals(accountNumber);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public void withdrawAmount(Account account, BigDecimal amount)
            throws BankTransactionException {
        BigDecimal newBalance = account.getCurrentBalance().subtract(amount);
        checkValidityAndThrowExceptionIfInsufficientBalance(newBalance, account);
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void depositAmount(Account account, BigDecimal amount)
            throws BankTransactionException {
        BigDecimal newBalance = account.getCurrentBalance().add(amount);
        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }


    @Override
    @Transactional(
//            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = BankTransactionException.class
    )
    public TransactionDto sendMoney(
            Account fromAccount,
            Account toAccount,
            TransferBalanceRequest transferBalanceRequest
    ) throws BankTransactionException {
        synchronized (this) {
            checkValidityAndThrowExceptionIfInvalidRequest(fromAccount, toAccount, transferBalanceRequest);
            withdrawAmount(fromAccount, transferBalanceRequest.getAmount());
            depositAmount(toAccount, transferBalanceRequest.getAmount());
            Transaction transaction = transactionRepository.save(
                    new Transaction(
                            0L,
                            transferBalanceRequest.getFromAccountNumber(),
                            transferBalanceRequest.getToAccountNumber(),
                            transferBalanceRequest.getAmount(),
                            new Timestamp(System.currentTimeMillis())
                    )
            );
            return TransactionMapper.toTransactionDto(transaction);
        }
    }


    @Override
    public AccountStatement getStatement(String accountNumber) throws BankTransactionException {
        Account account = accountRepository.findByAccountNumberEquals(accountNumber);
        if (account == null) {
            throw new BankTransactionException("Account not found " + accountNumber);
        }
        return new AccountStatement(
                account.getCurrentBalance(),
                TransactionMapper.toTransactionDtoList(
                        transactionRepository.findByDebitAccountNumberEqualsOrCreditAccountNumberEquals(
                                accountNumber,
                                accountNumber
                        )
                )
        );
    }


    private void checkValidityAndThrowExceptionIfInvalidRequest(Account fromAccount,
                                                                Account toAccount,
                                                                TransferBalanceRequest transferBalanceRequest)
            throws BankTransactionException {
        if (checkIfNegativeTransferAmountProvided(transferBalanceRequest.getAmount())) {
            throw new BankTransactionException("Balance Transfer amount should be positive.");
        }
        if (checkIfInvalidAccount(fromAccount)) {
            throw new BankTransactionException("From Account Number '" + transferBalanceRequest.getFromAccountNumber() + "' not found.");
        }
        if (checkIfInvalidAccount(toAccount)) {
            throw new BankTransactionException("To Account Number '" + transferBalanceRequest.getToAccountNumber() + "' not found.");
        }
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new BankTransactionException("You Cannot Send Money To Same Account.");
        }
    }

    private Boolean checkIfNegativeTransferAmountProvided(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private Boolean checkIfInvalidAccount(Account account) {
        if (account == null) {
            return true;
        }
        return false;
    }

    public void checkValidityAndThrowExceptionIfInsufficientBalance(BigDecimal newBalance, Account account)
            throws BankTransactionException {
        if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
            throw new BankTransactionException(
                    "The money in the account number '" + account.getAccountNumber() + "' is not enough (current balance: " + account.getCurrentBalance() + ")");
        }
    }
}
