package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreditType {
  PERSONAL("001", "PERSONAL"),
  BUSINESS("002", "BUSINESS"),
  CARD_BANK("003", "CARD_BANK");

  private final String code;
  private final String description;
}
