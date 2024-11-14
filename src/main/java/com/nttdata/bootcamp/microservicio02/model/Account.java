package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")

public class Account {
  @Id
  private String id = UUID.randomUUID().toString();
  private String accountNumber;
  private String customer;
  private AccountType accountType;
  private String currency;
  private BigDecimal amountAvailable;
  private Integer transactionLimit;
  private Double commissionRate;
  private Boolean active;
  private List<String> holders = new ArrayList<>();  // Lista de titulares
  private List<String> authorizedSigners = new ArrayList<>();

}