package net.chrisrichardson.bankingexample.apigateway.apigateway;

import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.AccountServiceProxy;
import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.CustomerServiceProxy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableConfigurationProperties(ApiGatewayDestinations.class)
public class ApiGatewayConfiguration {
  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public RouterFunction<ServerResponse> orderHistoryHandlerRouting(AccountHandlers accountHandlers) {
    return RouterFunctions.route(GET("/api/accountandcustomer/{accountId}"), accountHandlers::getAccountWithCustomer);
  }

  @Bean
  public AccountHandlers accountHandlers(AccountServiceProxy accountService, CustomerServiceProxy customerService) {
    return new AccountHandlers(accountService, customerService);
  }

  @Bean
  public RouteLocator customerProxyRouting(RouteLocatorBuilder builder, ApiGatewayDestinations apiGatewayDestinations) {
    return builder.routes()
            .route(r -> r.path("/api/customers/**").uri(apiGatewayDestinations.getCustomerServiceUrl()))
            .route(r -> r.path("/api/moneytransfers/**").uri(apiGatewayDestinations.getMoneyTransferServiceUrl()))
            .route(r -> r.path("/api/customerview/**").and().method("GET").uri(apiGatewayDestinations.getCustomerViewServiceUrl()))
            .route(r -> r.path("/api/accounts/**").uri(apiGatewayDestinations.getAccountServiceUrl()))
            .build();
  }

  @Bean
  RouterFunction<ServerResponse> routerFunction() {
    return  route(GET("/swagger-ui/index.html"), req ->
            ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html"))
                    .build());
  }

  @Bean
  public SwaggerHandlers swaggerHandlers(ApiGatewayDestinations apiGatewayDestinations, WebClient webClient) {
    return new SwaggerHandlers(webClient, apiGatewayDestinations);
  }

  @Bean
  public RouterFunction<ServerResponse> swaggerRouting(SwaggerHandlers swaggerHandlers) {
    return RouterFunctions.route(GET("/v3/api-docs"), swaggerHandlers::getSwagger);
  }

}
