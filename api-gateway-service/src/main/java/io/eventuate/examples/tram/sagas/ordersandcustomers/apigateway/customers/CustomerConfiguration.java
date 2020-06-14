package io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.customers;

import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.CustomerServiceProxy;
import io.eventuate.examples.tram.sagas.ordersandcustomers.apigateway.proxies.AccountServiceProxy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableConfigurationProperties(CustomerDestinations.class)
public class CustomerConfiguration {

  @Bean
  public RouterFunction<ServerResponse> orderHistoryHandlerRouting(AccountHandlers accountHandlers) {
    return RouterFunctions.route(GET("/api/accountandcustomer/{accountId}"), accountHandlers::getAccountWithCustomer);
  }

  @Bean
  public AccountHandlers orderHistoryHandlers(AccountServiceProxy accountService, CustomerServiceProxy customerService) {
    return new AccountHandlers(accountService, customerService);
  }

  @Bean
  public RouteLocator customerProxyRouting(RouteLocatorBuilder builder, CustomerDestinations customerDestinations) {
    return builder.routes()
            .route(r -> r.path("/api/customers/**").uri("http://customer-service:8080"))
            .route(r -> r.path("/api/moneytransfers/**").uri("http://money-transfer-service:8080"))
            .route(r -> r.path("/api/customerview/**").and().method("GET").uri("http://customer-view-service:8080"))
            .build();
  }
}
