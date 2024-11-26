package com.nttdata.bootcamp.microservicio02.service.impl;

import com.nttdata.bootcamp.microservicio02.config.WebClientHelper;
import com.nttdata.bootcamp.microservicio02.model.Account;
import com.nttdata.bootcamp.microservicio02.model.AccountType;
import com.nttdata.bootcamp.microservicio02.model.Credit;
import com.nttdata.bootcamp.microservicio02.model.CreditType;
import com.nttdata.bootcamp.microservicio02.model.Customer;
import com.nttdata.bootcamp.microservicio02.model.request.AccountRequest;
import com.nttdata.bootcamp.microservicio02.repository.AccountRepository;
import com.nttdata.bootcamp.microservicio02.service.AccountService;
import com.nttdata.bootcamp.microservicio02.utils.constant.ErrorCode;
import com.nttdata.bootcamp.microservicio02.utils.exception.OperationNoCompletedException;
import com.nttdata.bootcamp.microservicio02.utils.mapper.AccountMapper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

  public static final String PERSONAL = "PERSONAL";
  public static final String BUSINESS = "BUSINESS";
  public static final String VIP = "VIP";

  public static final String PYME = "PYME";

  private AccountRepository accountRepository;
  private WebClientHelper webClientHelper;

  public AccountServiceImpl(AccountRepository accountRepository, WebClientHelper webClientHelper) {
    this.accountRepository = accountRepository;
    this.webClientHelper = webClientHelper;
  }

  @Override
  public Mono<Account> create(AccountRequest accountRequest) {
    String customerId = accountRequest.getCustomer();

    log.info("Create an account in the service.");

    if (customerId.isBlank()) {
      log.warn("Client ID is empty");
      return Mono.empty();
    }
    Account account = AccountMapper.accountRequestToAccount(accountRequest);
    return webClientHelper
        .findByIdCustomerService(customerId)
        .flatMap(
            customer -> {
              account.setCustomer(customer.getId());
              return validateExistingAccount(customer, account)
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
                return Mono.empty();
              }
            });
  }

  private Mono<Account> createAccountByType(Account account, Customer customer) {
    setCommonAccountProperties(account); // Configura propiedades comunes de la cuenta

    if (PERSONAL.equals(customer.getCustomerType())) {
      return handlePersonalCustomer(account, customer);
    } else if (BUSINESS.equals(customer.getCustomerType())) {
      assignHoldersAndSigners(account, customer);
      return handleBusinessCustomer(account, customer);
    } else {
      return accountTypeNotAllowed();
    }
  }

  private Mono<Account> handlePersonalCustomer(Account account, Customer customer) {
    if (VIP.equals(customer.getCustomerSubType())
        && AccountType.SAVING.equals(account.getAccountType())) {
      return customerWithCardBankActive(customer)
          .flatMap(
              allowed -> {
                if (allowed) {
                  account.setIsDailyAverageMonth(true);
                  return accountRepository.insert(account);
                } else {
                  return accountTypeNotAllowed();
                }
              });
    }
    return accountRepository.insert(account);
  }

  private Mono<Account> handleBusinessCustomer(Account account, Customer customer) {
    return isBusinessAccountAllowed(account.getAccountType())
        .flatMap(
            allowed -> {
              if (allowed) {
                if (PYME.equals(customer.getCustomerSubType())) {
                  return customerWithCardBankActive(customer)
                      .flatMap(
                          exists ->
                              exists ? accountRepository.insert(account) : accountTypeNotAllowed());
                }
                return accountRepository.insert(account);
              } else {
                return accountTypeNotAllowed();
              }
            });
  }

  private Mono<Account> accountTypeNotAllowed() {
    log.warn("Account type not allowed for this customer");
    return Mono.error(
        new OperationNoCompletedException(
            ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getCode(),
            ErrorCode.ACCOUNT_TYPE_NO_ALLOWED.getMessage()));
  }

  private Mono<Boolean> customerWithCardBankActive(Customer customer) {

    return webClientHelper
        .findByIdCreditService(customer.getId())
        .filter(Credit::getActive)
        .filter(existingAccount -> existingAccount.getCreditType().equals(CreditType.CARD_BANK))
        .hasElements();
  }

  private Mono<Boolean> isBusinessAccountAllowed(AccountType accountType) {
    return Mono.just(AccountType.CURRENT.equals(accountType));
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

  @Override
  public Flux<Account> findByCustomerId(String id) {
    return accountRepository.findByCustomer(id);
  }
}
