package com.nttdata.bootcamp.microservicio02.expose;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("api/v1/account")
public class AccountController {

  private AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping({"/{id}/", "/{id}"})
  public Mono<Account> findbyId(@PathVariable("id") String id) {
    log.info("Find by id a account in the controller.");
    return accountService.findById(id);
  }

  @GetMapping({"", "/"})
  public Flux<Account> findAll() {
    log.info("List all accounts in the controller.");
    return accountService.findAll();
  }

  @PostMapping({"", "/"})
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Account> create(@RequestBody Account account) {
    log.info("Create an account in the controller.");
    return accountService.create(account);
  }

  @PutMapping({"/{id}/", "/{id}"})
  public Mono<ResponseEntity<Account>> update(
      @RequestBody Account account, @PathVariable("id") String accountId) {
    log.info("Update an account in the controller.");
    return accountService
        .update(account, accountId)
        .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @PatchMapping({"/{id}/", "/{id}"})
  public Mono<ResponseEntity<Account>> change(
      @RequestBody Account account, @PathVariable("id") String accountId) {
    log.info("Change an account in the controller.");
    return accountService
        .change(account, accountId)
        .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @DeleteMapping({"/{id}/", "/{id}"})
  public Mono<ResponseEntity<Account>> delete(@PathVariable("id") String id) {
    log.info("Delete an account in the controller.");
    return accountService
        .remove(id)
        .flatMap(account -> Mono.just(ResponseEntity.ok(account)))
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @GetMapping({"/customer/{id}/", "/customer/{id}"})
  public Flux<Account> findAccountsByCustomerId(@PathVariable("id") String customerId) {
    log.info("List all accounts in the controller.");
    return accountService.findByCustomerId(customerId);
  }
}
