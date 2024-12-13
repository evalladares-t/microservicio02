package com.nttdata.bootcamp.microservicio02.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
  WITHDRAWAL("001", "RETIRO"),
  DEPOSIT("002", "DEPOSITO"),
  BANK_TRANSFER("003", "TRANSFERENCIA BANCARIA"),
  // INTERBANK_TRANSFER("004", "TRANSFERENCIA INTERBANCARIA"),
  OPENING_AMOUNT("004", "MONTO DE APERTURA"),
  TRANSACTION_FEE("005", "COBRO POR TRANSACCION"),
  MAINTENANCE_PAYMENT("006", "PAGO DE MANTENIMIENTO");

  private final String code;
  private final String description;
}
