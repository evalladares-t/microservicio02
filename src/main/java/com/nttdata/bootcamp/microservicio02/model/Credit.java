package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
  private String id;
  private String customerId;
  private CreditType creditType;
  private Boolean active;
}
