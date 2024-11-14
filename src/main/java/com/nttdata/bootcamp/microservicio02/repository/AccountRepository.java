package com.nttdata.bootcamp.microservicio02.repository;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String> {

  Flux<Account> findByCustomerId(String id);

  Mono<Account> findAccountByCustomerId(Customer customer);
}
