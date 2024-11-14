package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
  private Customer customer;
  private AccountType accountType;
  private String currency;
  private Double amountAvailable;
  private Double minimumOpeningAmount;
  private Integer transactionLimit;
  private Double commissionRate;
  private String status;
  private List<Customer> holders = new ArrayList<>();  // Lista de titulares
  private List<Customer> authorizedSigners = new ArrayList<>();

}