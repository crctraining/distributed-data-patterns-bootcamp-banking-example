package net.chrisrichardson.bankingexample.apigateway.apigateway.proxies;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CustomerServiceProxy {
  private final CircuitBreaker cb;

  private WebClient client;
  private String customerServiceUrl;
  private TimeLimiter timeLimiter;

  public CustomerServiceProxy(WebClient client, CircuitBreakerRegistry circuitBreakerRegistry, String customerServiceUrl, TimeLimiterRegistry timeLimiterRegistry) {
    this.client = client;
    this.cb = circuitBreakerRegistry.circuitBreaker("MY_CIRCUIT_BREAKER");
    this.timeLimiter = timeLimiterRegistry.timeLimiter("MY_TIME_LIMITER");
    this.customerServiceUrl = customerServiceUrl;
  }

  public Mono<CustomerInfo> findCustomerById(long customerId) {
    Mono<ClientResponse> response = client
            .get()
            .uri(customerServiceUrl + "/api/customers/{customerId}", customerId)
            .exchange();
    return response.flatMap(resp -> {
      switch (resp.statusCode()) {
        case OK:
          return resp.bodyToMono(CustomerInfo.class);
        default:
          return Mono.error(new UnknownProxyException("Unknown: " + resp.statusCode()));
      }
    })
    .transformDeferred(TimeLimiterOperator.of(timeLimiter))
    .transformDeferred(CircuitBreakerOperator.of(cb))
    //.onErrorResume(CallNotPermittedException.class, e -> Mono.just(null))
    ;
  }
}
