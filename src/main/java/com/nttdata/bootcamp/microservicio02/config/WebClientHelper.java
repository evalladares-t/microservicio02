package com.nttdata.bootcamp.microservicio02.config;

import com.nttdata.bootcamp.microservicio02.model.Credit;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientHelper {

  private WebClient webClientCustomer;
  private WebClient webClientCredit;

  // private WebClient webClientTransaction;

  public WebClientHelper(WebClient webClientCustomer, WebClient webClientCredit) {
    this.webClientCustomer = webClientCustomer;
    this.webClientCredit = webClientCredit;
    // this.webClientTransaction = webClientTransaction;
  }

  public Mono<Customer> findByIdCustomerService(String id) {
    log.info("Getting client with id: [{}]", id);
    return this.webClientCustomer
        .get()
        .uri(uriBuilder -> uriBuilder.path("v1/customers/" + id).build())
        .retrieve()
        .bodyToMono(Customer.class)
        .onErrorResume(
            error -> {
              System.err.println("Error during call: " + error.getMessage());
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
              System.err.println("Error during call: " + error.getMessage());
              return Flux.empty();
            });
  }
}
