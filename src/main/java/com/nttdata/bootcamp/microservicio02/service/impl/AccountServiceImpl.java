package com.nttdata.bootcamp.microservicio02.service.impl;

import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.repository.AccountRepository;
import com.nttdata.bootcamp.microservicio02.service.AccountService;
import com.nttdata.bootcamp.microservicio02.utils.constant.ErrorCode;
import com.nttdata.bootcamp.microservicio02.utils.exception.OperationNoCompletedException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

  public static final String PERSONAL = "PERSONAL";
  public static final String BUSINESS = "BUSINESS";
  private AccountRepository accountRepository;
  private WebClient webClientCustomer;

  public AccountServiceImpl(AccountRepository accountRepository, WebClient webClientCustomer) {
    this.accountRepository = accountRepository;
    this.webClientCustomer = webClientCustomer;
  }

  @Override
  public Mono<Account> create(Account account) {
    String customerId = account.getCustomer();

    log.info("Create an account in the service.");

    if (customerId.isBlank()) {
      log.warn("Client ID is empty");
      return Mono.empty();
    }

    return findByIdCustomerService(customerId)
        .flatMap(
            customer -> {
              account.setCustomer(customer.getId());
              return validateExistingAccount(customer, account)
                  .flatMap(
                      existingAccount -> {
                        log.info("The client already has an account of this type");
                        return Mono
                            .<Account>
                                empty(); // Si ya existe una cuenta del mismo tipo, detiene el flujo
                      })
                  .switchIfEmpty(
                      createAccountByType(
                          account, customer)); // Intenta crear solo si no hay cuenta existente
            })
        .switchIfEmpty(
            Mono.error(
                new OperationNoCompletedException(
                    ErrorCode.ACCOUNT_NO_CREATED.getCode(),
                    ErrorCode.ACCOUNT_NO_CREATED.getMessage())))
        .doOnError(e -> log.error("Error creating account: ", e));
  }

  private Mono<Account> validateExistingAccount(Customer customer, Account account) {

    String customerType = customer.getCustomerType();

    // Si el cliente es empresarial
    if (BUSINESS.equals(customerType)) {
      return Mono.empty(); // Permite crear una nueva cuenta corriente
    }

    // Si el cliente es personal, verificamos que no tenga una cuenta del mismo tipo.
    return findByCustomerId(customer.getId())
        .filter(
            existingAccount -> existingAccount.getAccountType().equals(account.getAccountType()))
        .hasElements()
        .flatMap(
            exists -> {
              if (exists) {
                log.info("The client already has an account of this type");
                return Mono.error(
                    new OperationNoCompletedException(
                        ErrorCode.ACCOUNT_TYPE_ALREADY.getCode(),
                        ErrorCode.ACCOUNT_TYPE_ALREADY.getMessage()));
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
          .flatMap(
              allowed -> {
                if (allowed) {
                  return accountRepository.insert(account);
                } else {
                  log.warn("Account type not allowed for this customer");
                  return Mono.error(
                      new OperationNoCompletedException(
                          ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getCode(),
                          ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getMessage()));
                }
              });
    } else if (BUSINESS.equals(customerType)) {
      assignHoldersAndSigners(account, customer);

      return isBusinessAccountAllowed(accountType)
          .flatMap(
              allowed -> {
                if (allowed) {
                  return accountRepository.insert(account);
                } else {
                  log.warn("Account type not allowed for this customer");
                  return Mono.error(
                      new OperationNoCompletedException(
                          ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getCode(),
                          ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getMessage()));
                }
              });
    } else {
      log.warn("Account type not allowed for this customer");
      return Mono.error(
          new OperationNoCompletedException(
              ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getCode(),
              ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getMessage()));
    }
  }

  private Mono<Boolean> isPersonalAccountAllowed(String accountType, Customer customer) {
    // Verifica de forma reactiva si el cliente ya tiene una cuenta del mismo tipo
    return findByCustomerId(customer.getId())
        .filter(
            existingAccount ->
                existingAccount.getAccountType().getCode().equals("001")
                    || existingAccount.getAccountType().getCode().equals("002")
                    || existingAccount.getAccountType().getCode().equals("003"))
        .hasElements()
        .map(exists -> !exists); // Retorna true si NO existe ya una cuenta del mismo tipo
  }

  private Mono<Boolean> isBusinessAccountAllowed(String accountType) {
    return Mono.just("002".equals(accountType));
  }

  private void assignHoldersAndSigners(Account account, Customer customer) {

    List<String> holders = new ArrayList<>();
    holders.add(customer.getId());

    if (account.getHolders() != null && !account.getHolders().isEmpty()) {
      holders.addAll(account.getHolders());
    }

    List<String> authorizedSigners = new ArrayList<>();
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
    account.setActive(true);
    account.setAmountAvailable(BigDecimal.ZERO);
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
  public Mono<Account> update(Account account, String accountId) {
    log.info("Update an account in the service.");
    return accountRepository
        .findById(accountId)
        .flatMap(
            customerDB -> {
              account.setId(customerDB.getId());
              return accountRepository.save(account);
            })
        .switchIfEmpty(
            Mono.error(
                new OperationNoCompletedException(
                    ErrorCode.ACCOUNT_NO_UPDATE.getCode(),
                    ErrorCode.ACCOUNT_NO_UPDATE.getMessage())));
  }

  @Override
  public Mono<Account> change(Account account, String accountId) {
    log.info("Change an account in the service.");
    return accountRepository
        .findById(accountId)
        .flatMap(
            entidadExistente -> {
              // Iterar sobre los campos del objeto entidadExistente
              Field[] fields = account.getClass().getDeclaredFields();
              for (Field field : fields) {
                if ("id".equals(field.getName())) {
                  continue; // Saltar el campo 'id'
                }
                field.setAccessible(true); // Para acceder a campos privados
                try {
                  // Verificar si el valor del campo en entidadParcial no es null
                  Object value = field.get(account);
                  if (value != null) {
                    // Actualizar el campo correspondiente en entidadExistente
                    ReflectionUtils.setField(field, entidadExistente, value);
                  }
                } catch (IllegalAccessException e) {
                  e.printStackTrace(); // Manejo de errores si hay problemas con la reflexión
                }
              }
              // Guardar la entidad modificada
              return accountRepository.save(entidadExistente);
            })
        .switchIfEmpty(
            Mono.error(
                new OperationNoCompletedException(
                    ErrorCode.ACCOUNT_NO_UPDATE.getCode(),
                    ErrorCode.ACCOUNT_NO_UPDATE.getMessage())));
  }

  @Override
  public Mono<Account> remove(String accountId) {
    return accountRepository
        .findById(accountId)
        .flatMap(p -> accountRepository.deleteById(p.getId()).thenReturn(p));
  }

  public Mono<Customer> findByIdCustomerService(String id) {
    log.info("Getting client with id: [{}]", id);
    return this.webClientCustomer
        .get()
        .uri(uriBuilder -> uriBuilder.path("v1/customers/" + id).build())
        .retrieve()
        .bodyToMono(Customer.class);
  }

  @Override
  public Flux<Account> findByCustomerId(String id) {
    return accountRepository.findByCustomer(id);
  }
}
