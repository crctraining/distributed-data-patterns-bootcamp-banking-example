package net.chrisrichardson.bankingexample.apigateway.apigateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class SwaggerHandlers {
  private ApiGatewayDestinations apiGatewayDestinations;
  private WebClient client;

  public SwaggerHandlers(WebClient client, ApiGatewayDestinations apiGatewayDestinations) {
    this.client = client;
    this.apiGatewayDestinations = apiGatewayDestinations;
  }

  public Mono<ServerResponse> getSwagger(ServerRequest serverRequest) {

    ObjectMapper outOm = new ObjectMapper(new YAMLFactory());


    List<String> destinations = Arrays.asList(apiGatewayDestinations.getAccountServiceUrl(),
            apiGatewayDestinations.getCustomerServiceUrl(),
            apiGatewayDestinations.getMoneyTransferServiceUrl(),
            apiGatewayDestinations.getCustomerViewServiceUrl()
    );

    Mono<List<Map<String, Object>>> jsonMaps = Flux.fromIterable(destinations)
            .flatMap(this::getServiceSwagger)
            .collectList();

    Mono<Map<String, Object>> jsonMap = jsonMaps.map(this::combine);

    return jsonMap.flatMap(json -> {
      try {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(outOm.writeValueAsString(json)));
      } catch (JsonProcessingException e) {
        return Mono.error(e);
      }
    })
    ;

  }

  private Map<String, Object> combine(List<Map<String, Object>> maps) {
    Map<String, Object> result = new HashMap<>();
    for (Map<String, Object> map : maps) {
      MapUtil.mergeIntoMap(result, map);
    }
    result.put("host", "localhost:8080");
    return result;
  }

  private Mono<Map<String, Object>> getServiceSwagger(String serviceUrl) {
    ObjectMapper inOm = new ObjectMapper(new YAMLFactory());
    Mono<ClientResponse> response = client
            .get()
            .uri(serviceUrl + "/v3/api-docs")
            .exchange();

    return response.flatMap(resp -> {
      if (resp.statusCode() == HttpStatus.OK) {
        return resp.bodyToMono(String.class).flatMap(json -> {
          try {
            return Mono.just((Map<String, Object>)inOm.readValue(json, Map.class));
          } catch (JsonProcessingException e) {
            return Mono.error(e);
          }
        });
      } else
        return Mono.error(new RuntimeException("Bad status: " + resp.statusCode()));
    });
  }

}
