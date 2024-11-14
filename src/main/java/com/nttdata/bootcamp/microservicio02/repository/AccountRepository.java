package com.nttdata.bootcamp.microservicio02.repository;

import com.nttdata.bootcamp.microservicio02.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account,String> {

  Flux<Account> findByCustomer(String id);

}
