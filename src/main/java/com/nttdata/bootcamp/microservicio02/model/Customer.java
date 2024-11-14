package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Customer {

  private String id;
  private String customerType;
  private String firstName;
  private String lastName;
  private DocumentIdentity documentIdentity;
  private boolean isActive;
}