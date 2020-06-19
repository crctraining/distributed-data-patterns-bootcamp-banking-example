package net.chrisrichardson.bankingexample.apigateway.apigateway;

import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.AccountServiceProxy;
import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.apigateway.AccountWithCustomer;
import net.chrisrichardson.bankingexample.accountservice.common.GetAccountResponse;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
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
    throw new RuntimeException("not yet implemented");
  }


}
