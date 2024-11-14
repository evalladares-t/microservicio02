package com.nttdata.bootcamp.microservicio02.expose;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("api/v1")
public class AccountController {

  private AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/customers/{id}")
  public Mono<Customer> findByIdCustomerService(@PathVariable("id") String customerId) {
    log.info("Obtengo customer by id:", customerId);
    return accountService.findByIdCustomerService(customerId);
  }


  @GetMapping("/accounts/{id}")
  public Mono<Account> byId(@PathVariable("id") String id) {
    log.info("byId>>>>>");
    return accountService.findById(id);
  }

  @GetMapping("/accounts-all")
  public Flux<Account> findAll() {
    log.info("findAll>>>>>");
    return accountService.findAll();
  }
  @PostMapping("/account/")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Account> create(@RequestBody Account account) {
    log.info("create>>>>>");
    return accountService.create(account);
  }

  @PutMapping("/account/")
  public Mono<ResponseEntity<Account>> update(@RequestBody Account account) {
    log.info("update>>>>>");
    return accountService.update(account)
            .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
  @PatchMapping("/accounts")
  public Mono<ResponseEntity<Account>> change(@RequestBody Account account) {
    log.info("change>>>>>");
    return accountService.change(account)
            .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @DeleteMapping("/api/v1/accounts/{id}")
  public Mono<ResponseEntity<Account>> delete(@PathVariable("id") String id) {
    log.info("delete>>>>>");
    return accountService.remove(id)
            .flatMap(account -> Mono.just(ResponseEntity.ok(account)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

}
