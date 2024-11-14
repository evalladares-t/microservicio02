package com.nttdata.bootcamp.microservicio02.service;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

  Mono<Account> create(Account account);

  Mono<Account> findById(String accountId);

  Mono<Account> findAccountByCustomerId(Customer customerId);

  Flux<Account> findAll();

  Mono<Account> update(Account account);

  Mono<Account> change(Account account);

  Mono<Account> remove(String accountId);

  Mono<Customer> findByIdCustomerService(String customerId);

  Flux<Account> findByCustomerId(String id);

}
