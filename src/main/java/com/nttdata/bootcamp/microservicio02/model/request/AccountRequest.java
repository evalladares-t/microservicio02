package com.nttdata.bootcamp.microservicio02.model.request;

import com.nttdata.bootcamp.microservicio02.model.AccountType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
  private String accountNumber;
  private String customer;
  private AccountType accountType;
  private String currency;
  private BigDecimal amountAvailable;
  private Integer transactionLimit;
  private Double commissionRate;
  private Boolean active;
  private Integer dateAllowedTransaction;
  private BigDecimal openingAmount;
  private List<String> holders = new ArrayList<>(); // Lista de titulares
  private List<String> authorizedSigners = new ArrayList<>(); // Lista de Firmantes autorizados
}