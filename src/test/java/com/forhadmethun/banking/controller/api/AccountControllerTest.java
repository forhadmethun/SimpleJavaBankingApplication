package com.forhadmethun.banking.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.forhadmethun.banking.controller.request.AccountStatementRequest;
import com.forhadmethun.banking.controller.request.TransferBalanceRequest;
import com.forhadmethun.banking.dto.mapper.AccountMapper;
import com.forhadmethun.banking.dto.mapper.TransactionMapper;
import com.forhadmethun.banking.dto.model.AccountDto;
import com.forhadmethun.banking.dto.model.AccountStatement;
import com.forhadmethun.banking.model.Account;
import com.forhadmethun.banking.model.Transaction;
import com.forhadmethun.banking.service.impl.AccountServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
@WebAppConfiguration
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @Autowired
    private WebApplicationContext wac;
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).dispatchOptions(true).build();
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesAccountController() {
        final ServletContext servletContext = wac.getServletContext();
        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("accountController"));
    }
    @Test
    public void givenGetAllAccountURI_whenMockMVC_thenVerifyResponse() throws Exception {
        Account account1 = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();

        doReturn(Arrays.asList(AccountMapper.toAccountDto(account1))).when(accountService).findAll();

        mockMvc.perform(get("/api/account/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":[{\"accountNumber\":\"1001\",\"currentBalance\":50000,\"accountName\":null}]}"))

        ;
    }
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Test
    public void createAccountCheck() throws Exception {
        Account account1 = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();

        doReturn(AccountMapper.toAccountDto(account1)).when(accountService).save(AccountMapper.toAccountDto(account1));
        doReturn(Arrays.asList(AccountMapper.toAccountDto(account1))).when(accountService).findAll();

        mockMvc.perform(
                post("/api/account/create")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(
                                        AccountDto.builder()
                                                .accountNumber("1001")
                                                .currentBalance(new BigDecimal(50000))
                                                .build()
                                )
                        )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":[{\"accountNumber\":\"1001\",\"currentBalance\":50000,\"accountName\":null}]}"))

        ;
    }

    @Test
    public void sendMoneyCheck() throws Exception{

        Account fromAccount = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();
        Account toAccount = Account.builder()
                .accountNumber("2002")
                .currentBalance(new BigDecimal(2000))
                .build();

        TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                new BigDecimal(3000)
        );

        Transaction withdrawTranscation = Transaction.builder()
                .account(fromAccount)
                .transactionAmount(transferBalanceRequest.getAmount())
                .transactionDateTime(new Timestamp(System.currentTimeMillis()))
                .description("Credited to account no " + transferBalanceRequest.getToAccountNumber())
                .build();

        doReturn(AccountMapper.toAccountDto(fromAccount)).when(accountService).save(AccountMapper.toAccountDto(fromAccount));
        doReturn(fromAccount).when(accountService).findByAccountNumber(fromAccount.getAccountNumber());

        doReturn(AccountMapper.toAccountDto(toAccount)).when(accountService).save(AccountMapper.toAccountDto(toAccount));
        doReturn(toAccount).when(accountService).findByAccountNumber(toAccount.getAccountNumber());

        doReturn(TransactionMapper.toTransactionDto(withdrawTranscation)).when(accountService).sendMoney(fromAccount,toAccount,transferBalanceRequest);


        mockMvc.perform(
                post("/api/account/send-money")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(transferBalanceRequest)
                        ))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getStatementCheck() throws Exception{
        Account account1 = Account.builder()
                .accountNumber("1001")
                .currentBalance(new BigDecimal(50000))
                .build();

        doReturn(AccountMapper.toAccountDto(account1)).when(accountService).save(AccountMapper.toAccountDto(account1));
        doReturn(account1).when(accountService).findByAccountNumber(account1.getAccountNumber());
        doReturn(new AccountStatement(account1.getCurrentBalance(), null)).when(accountService).getStatement(account1.getAccountNumber());

        mockMvc.perform(
                post("/api/account/statement")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(
                                asJsonString(
                                        AccountStatementRequest.builder()
                                        .accountNumber(account1.getAccountNumber())
                                        .build()
                                )
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"OK\",\"payload\":{\"currentBalance\":50000,\"transactionHistory\":null}}"))
        ;


    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            String requestJson=ow.writeValueAsString(obj);
            return requestJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
