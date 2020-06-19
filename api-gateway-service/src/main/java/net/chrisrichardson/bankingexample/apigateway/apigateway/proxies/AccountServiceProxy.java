package net.chrisrichardson.bankingexample.apigateway.apigateway.proxies;

import net.chrisrichardson.bankingexample.apigateway.apigateway.ApiGatewayDestinations;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import net.chrisrichardson.bankingexample.accountservice.common.GetAccountResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class AccountServiceProxy {
  private final CircuitBreaker circuitBreaker;
  private final TimeLimiter timeLimiter;
  private final ApiGatewayDestinations apiGatewayDestinations;

  private WebClient client;

  public AccountServiceProxy(ApiGatewayDestinations apiGatewayDestinations, WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
    this.apiGatewayDestinations = apiGatewayDestinations;
    this.client = client;
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("MY_CIRCUIT_BREAKER");
    this.timeLimiter = timeLimiterRegistry.timeLimiter("MY_TIME_LIMITER");
  }



  public Mono<Optional<GetAccountResponse>> findAccountById(String accountId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(apiGatewayDestinations.getAccountServiceUrl() + "/api/accounts/{accountId}", accountId)
            .exchange();

    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(GetAccountResponse.class).map(Optional::of);
        case NOT_FOUND:
          Mono<Optional<GetAccountResponse>> notFound = Mono.just(Optional.empty());
          return notFound;
        default:
          return Mono.error(new UnknownProxyException("Unknown: " + resp.statusCode()));
      }
    })
    .transformDeferred(TimeLimiterOperator.of(timeLimiter))
    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    ;
  }
}
