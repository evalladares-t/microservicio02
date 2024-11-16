package com.nttdata.bootcamp.microservicio02.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OperationNoCompletedException extends  RuntimeException{

  private final String errorCode;
  private final String errorMessage;

}
