package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers;

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

    Mono<Optional<GetAccountResponse>> x = orderService.findAccountById(accountId);
    Mono<Optional<Mono<AccountWithCustomer>>> y = x
            .map(maybeAccount ->
            {
              Optional<GetAccountResponse> z = maybeAccount;
              Optional<Mono<AccountWithCustomer>> accountWithCustomerMono = z.map(account ->
              {
                Mono<AccountWithCustomer> objectMono = customerService.findCustomerById(account.getAccountInfo().getCustomerId())
                        .map(customer -> new AccountWithCustomer(account, customer));
                return objectMono;
              });
              return accountWithCustomerMono;
            });
    Mono<ServerResponse> z = y
            .flatMap(maybeAccountWithCustomer ->
                    maybeAccountWithCustomer.map(accountWithCustomer ->
                            accountWithCustomer.flatMap((AccountWithCustomer n) -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(fromValue(n))))
                            .orElseGet(() -> ServerResponse.notFound().build()));
    return z;
  }
}
