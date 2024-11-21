package com.nttdata.bootcamp.microservicio02.config;

import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.model.dto.TransactionCreateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientHelper {

  private WebClient webClientCustomer;
  private WebClient webClientTransaction;

  public WebClientHelper(WebClient webClientCustomer, WebClient webClientTransaction) {
    this.webClientCustomer = webClientCustomer;
    this.webClientTransaction = webClientTransaction;
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

  public Mono<TransactionCreateDto> createTransactionWithAccountId(
      TransactionCreateDto transactionCreateDto) {
    log.info("Create transaction with accountId: [{}]", transactionCreateDto.getAccountId());
    return this.webClientTransaction
        .post()
        .uri(uriBuilder -> uriBuilder.path("v1/transactions/").build())
        .bodyValue(transactionCreateDto)
        .retrieve()
        .bodyToMono(TransactionCreateDto.class);
  }
}
