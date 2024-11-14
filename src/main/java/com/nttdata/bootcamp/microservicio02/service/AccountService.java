package com.nttdata.bootcamp.microservicio02.service;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

  Mono<Account> create(Account account);

  Mono<Account> findById(String accountId);

  Flux<Account> findAll();

  Mono<Account> update(Account account, String accountId);

  Mono<Account> change(Account account, String accountId);

  Mono<Account> remove(String accountId);

  Flux<Account> findByCustomerId(String id);

}
