package com.forhadmethun.banking.repository;

import com.forhadmethun.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDebitAccountNumberEqualsOrCreditAccountNumberEquals(
            String debitAccountNumber, String creditAccountNumber
    );

}
