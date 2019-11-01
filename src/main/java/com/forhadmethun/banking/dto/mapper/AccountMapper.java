package com.forhadmethun.banking.dto.mapper;

import com.forhadmethun.banking.dto.model.AccountDto;
import com.forhadmethun.banking.model.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {
    public static AccountDto toAccountDto(Account account){
        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .currentBalance(account.getCurrentBalance())
                .build();
    }
    public static List<AccountDto> toAccountDtoList(List<Account> accountList){
        return accountList.stream()
                .map(account -> toAccountDto(account))
                .collect(Collectors.toList());
    }
}