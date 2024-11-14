package com.nttdata.bootcamp.microservicio02.service.impl;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.AccountType;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.repository.AccountRepository;
import com.nttdata.bootcamp.microservicio02.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

  public static final String PERSONAL = "PERSONAL";
  public static final String  BUSINESS= "BUSINESS";
  private AccountRepository accountRepository;
  private WebClient webClientCustomer;

  public AccountServiceImpl (AccountRepository accountRepository, WebClient webClientCustomer) {
    this.accountRepository = accountRepository;
    this.webClientCustomer = webClientCustomer;
  }

  @Override
  public Mono<Account> create(Account account) {
    String customerId = account.getCustomer().getId();

    if (customerId.isBlank()) {
      log.warn("Client ID is empty");
      return Mono.empty();
    }

    return findByIdCustomerService(customerId)
            .flatMap(customer -> {
              account.setCustomer(customer);
              return validateExistingAccount(customerId, account)
                      .flatMap(existingAccount -> {
                        log.info("The client already has an account of this type");
                        return Mono.<Account>empty(); // Si ya existe una cuenta del mismo tipo, detiene el flujo
                      })
                      .switchIfEmpty(createAccountByType(account, customer)); // Intenta crear solo si no hay cuenta existente
            })
            .doOnError(e -> log.error("Error creating account: ", e));
  }

  private Mono<Account> validateExistingAccount(String customerId, Account account) {
    String accountTypeCode = account.getAccountType().getCode();
    String customerType = account.getCustomer().getCustomerType();

    // Si el cliente es empresarial
    if (BUSINESS.equals(customerType)) {
      return Mono.empty(); // Permite crear una nueva cuenta corriente
    }

    // Si el cliente es personal, verificamos que no tenga una cuenta del mismo tipo.
    return findByCustomerId(customerId)
            .filter(existingAccount -> existingAccount.getAccountType().equals(account.getAccountType()))
            .hasElements()
            .flatMap(exists -> {
              if (exists) {
                log.info("The client already has an account of this type");
                return Mono.empty();
              } else {
                return Mono.just(account);
              }
            });
  }

  private Mono<Account> createAccountByType(Account account, Customer customer) {
    setCommonAccountProperties(account); // Configura propiedades comunes de la cuenta

    String accountType = account.getAccountType().getCode();
    String customerType = customer.getCustomerType();

    if (PERSONAL.equals(customerType)) {
      return isPersonalAccountAllowed(accountType, customer)
              .flatMap(allowed -> {
                if (allowed) {
                  return accountRepository.insert(account);
                } else {
                  log.warn("Account type not allowed for this customer");
                  return Mono.empty();
                }
              });
    } else if (BUSINESS.equals(customerType)) {
      assignHoldersAndSigners(account, customer);

      return isBusinessAccountAllowed(accountType)
              .flatMap(allowed -> {
                if (allowed) {
                  return accountRepository.insert(account);
                } else {
                  log.warn("Account type not allowed for this customer");
                  return Mono.empty();
                }
              });
    } else {
      log.warn("Account type not allowed for this customer");
      return Mono.empty();
    }
  }
  private Mono<Boolean> isPersonalAccountAllowed(String accountType, Customer customer) {
    // Verifica de forma reactiva si el cliente ya tiene una cuenta del mismo tipo
    return findByCustomerId(customer.getId())
            .filter(existingAccount -> existingAccount.getAccountType().getCode().equals("001") ||
                    existingAccount.getAccountType().getCode().equals("002") ||
                    existingAccount.getAccountType().getCode().equals("003"))
            .hasElements()
            .map(exists -> !exists);  // Retorna true si NO existe ya una cuenta del mismo tipo
  }

  private Mono<Boolean> isBusinessAccountAllowed(String accountType) {
    return Mono.just("002".equals(accountType));
  }

  private void assignHoldersAndSigners(Account account, Customer customer) {

    List<Customer> holders = new ArrayList<>();
    holders.add(customer);

    if (account.getHolders() != null && !account.getHolders().isEmpty()) {
      holders.addAll(account.getHolders());
    }

    List<Customer> authorizedSigners = new ArrayList<>();
    if (account.getAuthorizedSigners() != null && !account.getAuthorizedSigners().isEmpty()) {
      authorizedSigners.addAll(account.getAuthorizedSigners());
    }

    // Asignamos las listas de titulares y firmantes a la cuenta
    account.setHolders(holders);
    account.setAuthorizedSigners(authorizedSigners);
  }

  private void setCommonAccountProperties(Account account) {
    Random accountNumberRandom = new Random();
    account.setAccountNumber(Long.toString(accountNumberRandom.nextLong()));
    account.setCurrency("Soles");
    account.setStatus("true");
    account.setAmountAvailable(0.0);
  }

  @Override
  public Mono<Account> findById(String accountId) {
    return accountRepository.findById(accountId);
  }

  @Override
  public Flux<Account> findAll() {
    return accountRepository.findAll();
  }

  @Override
  public Mono<Account> update(Account account) {
    return accountRepository.save(account);
  }

  @Override
  public Mono<Account> change(Account account) {
    return accountRepository.findById(account.getId())
            .flatMap(accountDB -> {
              return create(account);
            })
            .switchIfEmpty(Mono.empty());
  }

  @Override
  public Mono<Account> remove(String accountId) {
    return accountRepository
            .findById(accountId)
            .flatMap(p -> accountRepository.deleteById(p.getId()).thenReturn(p));
  }

  @Override
  public Mono<Customer> findByIdCustomerService(String id) {
    log.info("Getting client with id: [{}]", id);
    return this.webClientCustomer.get()
            .uri(uriBuilder -> uriBuilder
                    .path("v1/customers/"+ id)
                    .build())
            .retrieve()
            .bodyToMono(Customer.class);
  }

  @Override
  public Flux<Account> findByCustomerId(String id) {
    return accountRepository.findByCustomerId(id);
  }

  @Override
  public Mono<Account> findAccountByCustomerId(Customer customerId) {
    return accountRepository.findAccountByCustomerId(customerId);
  }



}
