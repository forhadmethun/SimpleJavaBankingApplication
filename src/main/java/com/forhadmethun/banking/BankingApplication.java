package com.forhadmethun.banking;

import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BankingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

	@Component
	public class DatabaseSeeder implements CommandLineRunner {
		@Autowired private AccountRepository accountRepository;
		@Autowired
		public DatabaseSeeder(AccountRepository accountRepository){
			this.accountRepository =accountRepository;
		}

		@Override
		public void run(String... args) throws Exception {
			List<Account> accounts = new ArrayList<Account>();

			accounts.add(Account.builder().accountNumber("10011").currentBalance(new BigDecimal("1000")).build());
			accounts.add(Account.builder().accountNumber("10012").currentBalance(new BigDecimal("1000")).build());
			accounts.add(Account.builder().accountNumber("10013").currentBalance(new BigDecimal("1000")).build());

			accountRepository.save(accounts.get(0));
			accountRepository.save(accounts.get(1));
			accountRepository.save(accounts.get(2));

		}
	}
}
