package com.nttdata.bootcamp.microservicio02.utils.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  DATA_NOT_FOUND("404", "Data not found"),
  INVALID_REQUEST("400", "Invalid request parameters"),
  ACCOUNT_NO_CREATED("404", "The account was not created"),
  ACCOUNT_TYPE_ALREADY("404", "The client already has an account of this type"),
  ACCOUNT_NO_UPDATE("404", "The account was not update"),
  ACCOUNT_NO_DELETED("404", "The account was not deleted"),
  ACCOUNT_NO_COMPLETED("404", "Operación no completada"),
  ACCOUNT_TYPE_NO_ALLOWED("404", "Account type not allowed for this customer"),

  INTERNAL_SERVER_ERROR("500", "Internal server error"),
  SERVICE_UNAVAILABLE("503", "Service unavailable");

  private final String code;
  private final String message;
}
