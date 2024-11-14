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
@RequestMapping("api/v1/account")
public class AccountController {

  private AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/{id}")
  public Mono<Account> findbyId(@PathVariable("id") String id) {
    log.info("Find by id a account in the controller.");
    return accountService.findById(id);
  }

  @GetMapping("/list")
  public Flux<Account> findAll() {
    log.info("List all accounts in the controller.");
    return accountService.findAll();
  }
  @PostMapping("/")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Account> create(@RequestBody Account account) {
    log.info("Create an account in the controller.");
    return accountService.create(account);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<Account>> update(@RequestBody Account account, @PathVariable("id") String accountId) {
    log.info("Update an account in the controller.");
    return accountService.update(account, accountId)
            .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
  @PatchMapping("/{id}")
  public Mono<ResponseEntity<Account>> change(@RequestBody Account account, @PathVariable("id") String accountId) {
    log.info("Change an account in the controller.");
    return accountService.change(account, accountId)
            .flatMap(accountUpdate -> Mono.just(ResponseEntity.ok(accountUpdate)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  @DeleteMapping("/api/v1/accounts/{id}")
  public Mono<ResponseEntity<Account>> delete(@PathVariable("id") String id) {
    log.info("Delete an account in the controller.");
    return accountService.remove(id)
            .flatMap(account -> Mono.just(ResponseEntity.ok(account)))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

}
