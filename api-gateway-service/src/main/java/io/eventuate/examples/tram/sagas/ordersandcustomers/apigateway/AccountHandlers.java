package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.AccountServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.apigateway.AccountWithCustomer;
import net.chrisrichardson.bankingexample.accountservice.common.GetAccountResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class AccountHandlers {

  private AccountServiceProxy orderService;
  private CustomerServiceProxy customerService;

  public AccountHandlers(AccountServiceProxy orderService, CustomerServiceProxy customerService) {
    this.orderService = orderService;
    this.customerService = customerService;
  }

  @NotNull
  public Mono<ServerResponse> getAccountWithCustomer(ServerRequest serverRequest) {
    String accountId = serverRequest.pathVariable("accountId");

    Mono<Optional<GetAccountResponse>> accountResponse = orderService.findAccountById(accountId);

    Mono<Optional<AccountWithCustomer>> accountWithCustomerResponse = accountResponse
            .flatMap(maybeAccount ->
                    maybeAccount.map(account ->
                            customerService.findCustomerById(account.getAccountInfo().getCustomerId())
                                    .map(customer -> Optional.of(new AccountWithCustomer(account, customer))))
                    .orElseGet(() -> Mono.just(Optional.empty())));

    return accountWithCustomerResponse
            .flatMap(maybeAccountWithCustomer ->
                    maybeAccountWithCustomer
                            .map(accountWithCustomer ->
                               ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(fromValue(accountWithCustomer)))
                            .orElseGet(() -> ServerResponse.notFound().build()));
  }


}
