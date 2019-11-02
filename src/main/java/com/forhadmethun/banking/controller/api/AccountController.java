package com.forhadmethun.banking.controller.api;

import com.forhadmethun.banking.controller.request.AccountStatementRequest;
import com.forhadmethun.banking.controller.request.TransferBalanceRequest;
import com.forhadmethun.banking.dto.response.Response;
import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.exception.BankTransactionException;
import com.forhadmethun.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public Response<Object> create(@RequestBody Account account)
            throws BankTransactionException {
        try {
            accountService.save(account);
            return Response.ok().setPayload(
                    accountService.findAll()
            );
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Response all() {
        return Response.ok().setPayload(
                accountService.findAll()
        );
    }

    @PostMapping("/send-money")
    public Response sendMoney(
            @RequestBody TransferBalanceRequest transferBalanceRequest
    ) {

        try {
            Account fromAccount = accountService.findByAccountNumber(transferBalanceRequest.getFromAccountNumber());
            Account toAccount = accountService.findByAccountNumber(transferBalanceRequest.getToAccountNumber());
            return Response.ok().setPayload(
                    accountService.sendMoney(
                            fromAccount,
                            toAccount,
                            transferBalanceRequest
                    )
            );
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }

    @PostMapping("/statement")
    public Response getStatement(
            @RequestBody AccountStatementRequest accountStatementRequest

    ) {
        try {
            return Response.ok().setPayload(
                    accountService.getStatement(
                            accountStatementRequest.getAccountNumber()
                    )
            );
        } catch (BankTransactionException e) {
            return Response.exception().setErrors(e.getMessage());
        }
    }
}
