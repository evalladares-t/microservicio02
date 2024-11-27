package com.nttdata.bootcamp.microservicio02.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  private String accountId;
  private String transactionType;
  private BigDecimal amount;
}
