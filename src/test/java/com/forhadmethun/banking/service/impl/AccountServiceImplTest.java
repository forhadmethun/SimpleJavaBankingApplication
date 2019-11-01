package com.forhadmethun.banking.service.impl;

import com.forhadmethun.banking.controller.request.TransferBalanceRequest;
import com.forhadmethun.banking.exception.BankTransactionException;
import com.forhadmethun.banking.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class AccountServiceImplTest {

    @TestConfiguration
    static class AccountServiceTestContextConfiguration {
        @Bean
        public AccountServiceImpl accountServiceImplTest() {
            return new AccountServiceImpl();

        }
    }

    @Autowired
    private AccountServiceImpl accountService;


    @Test
    public void sendMoneyTest() throws BankTransactionException{
        Account account1 = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();
        Account account2 = Account.builder()
                .accountNumber("2002")
                .currentBalance(new BigDecimal(2000))
                .build();

        accountService.save(account1);
        accountService.save(account2);

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(3000)
                );
        Account fromAccount = accountService.findByAccountNumber(account1.getAccountNumber());
        Account toAccount = accountService.findByAccountNumber(account2.getAccountNumber());
        accountService.sendMoney(fromAccount,toAccount,transferBalanceRequest);
        assertThat(
                accountService
                        .findByAccountNumber(account1.getAccountNumber())
                        .getCurrentBalance())
                        .isEqualTo(new BigDecimal(47000));
        assertThat(
                accountService
                        .findByAccountNumber(account2.getAccountNumber())
                        .getCurrentBalance())
                        .isEqualTo(new BigDecimal(5000));

    }

    @Test
    public void getStatement() throws BankTransactionException {
        Account account1 = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();
        Account account2 = Account.builder()
                .accountNumber("2002")
                .currentBalance(new BigDecimal(2000))
                .build();

        accountService.save(account1);
        accountService.save(account2);

        TransferBalanceRequest transferBalanceRequest =
                new TransferBalanceRequest(
                        account1.getAccountNumber(),
                        account2.getAccountNumber(),
                        new BigDecimal(3000)
                );

        Account fromAccount = accountService.findByAccountNumber(account1.getAccountNumber());
        Account toAccount = accountService.findByAccountNumber(account2.getAccountNumber());
        accountService.sendMoney(fromAccount,toAccount,transferBalanceRequest);
        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance())
                .isEqualTo(new BigDecimal(47000));

        accountService.sendMoney(fromAccount,toAccount,transferBalanceRequest);

        assertThat(accountService.getStatement(account1.getAccountNumber())
                .getCurrentBalance()).isEqualTo(new BigDecimal(44000));

        assertThat(accountService.getStatement(account2.getAccountNumber())
                .getCurrentBalance()).isEqualTo(new BigDecimal(8000));

    }

}
