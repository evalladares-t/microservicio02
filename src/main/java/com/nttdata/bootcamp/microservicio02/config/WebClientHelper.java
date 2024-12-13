package com.nttdata.bootcamp.microservicio02.config;

import com.nttdata.bootcamp.microservicio02.model.Credit;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientHelper {

  @Autowired private WebClient webClientCustomer;

  @Autowired private WebClient webClientCredit;

  @Autowired private WebClient webClientTransaction;

  public Mono<Customer> findByIdCustomerService(String id) {
    log.info("Getting client with id: [{}]", id);
    return this.webClientCustomer
        .get()
        .uri(uriBuilder -> uriBuilder.path("v1/customers/" + id).build())
        .retrieve()
        .bodyToMono(Customer.class)
        .onErrorResume(
            error -> {
              log.error("Error during call: " + error.getMessage());
              return Mono.empty();
            });
  }

  public Flux<Credit> findByIdCreditService(String id) {
    log.info("Getting credit with id: [{}]", id);
    return this.webClientCredit
        .get()
        .uri(uriBuilder -> uriBuilder.path("v1/credits/customer/" + id).build())
        .retrieve()
        .bodyToFlux(Credit.class)
        .onErrorResume(
            error -> {
              log.error("Error during call: " + error.getMessage());
              return Flux.empty();
            });
  }

  public Mono<Transaction> createTransactionWithOpeningAmount(Transaction transaction) {
    log.info("Create Transaction with Opening Ammount");
    return this.webClientTransaction
        .post()
        .uri(uriBuilder -> uriBuilder.path("v1/transactions").build())
        .bodyValue(transaction)
        .retrieve()
        .bodyToMono(Transaction.class)
        .onErrorResume(
            error -> {
              log.error("Error during call: " + error.getMessage());
              return Mono.empty();
            });
  }
}
