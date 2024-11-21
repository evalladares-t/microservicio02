package com.nttdata.bootcamp.microservicio02.utils.mapper;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.request.AccountRequest;
import java.math.BigDecimal;
import org.springframework.beans.BeanUtils;

public class AccountMapper {
  public static Account toDTO(AccountRequest accountRequest) {
    Account account = new Account();
    BeanUtils.copyProperties(accountRequest, account);
    account.setAmountAvailable(BigDecimal.ZERO);
    if (accountRequest.getOpeningAmount() != null
        && accountRequest.getOpeningAmount().compareTo(BigDecimal.ZERO) > 0) {
      account.setAmountAvailable(accountRequest.getOpeningAmount());
    }
    return account;
  }
}
