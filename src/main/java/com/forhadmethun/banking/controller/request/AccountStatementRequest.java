package com.forhadmethun.banking.controller.request;
import lombok.*;

import java.math.BigDecimal;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementRequest {

    private String accountNumber;

}
