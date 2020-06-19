package net.chrisrichardson.bankingexample.apigateway.apigateway;

import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.CustomerServiceProxy;
import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.ProxyConfiguration;
import net.chrisrichardson.bankingexample.apigateway.apigateway.proxies.UnknownProxyException;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.apigateway.AccountWithCustomer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountHandlersTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "api.gateway.destinations.customerServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.accountServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.moneyTransferServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.customerViewServiceUrl=http://localhost:${wiremock.server.port}"
        })
@AutoConfigureWireMock(port = 0)
public class AccountHandlersTest {

  @Configuration
  @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
  @Import(ProxyConfiguration.class)
  static public class Config {
  }

  @Autowired
  private CustomerServiceProxy customerServiceProxy;

  @Autowired
  private WebClient webClient;

  @LocalServerPort
  private int port;


  @Test
  public void shouldGetAccountWithCustomer() throws JSONException {

    JSONObject customerJSon = makeCustomerJSon();
    String customerJSonResponse = customerJSon.toString();

    JSONObject accountJSon = makeAccountJSon();
    String accountJSonResponse = accountJSon.toString();

    stubFor(get(urlEqualTo("/api/customers/101"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(customerJSonResponse)));

    stubFor(get(urlEqualTo("/api/accounts/102"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(accountJSonResponse)));


    Mono<ClientResponse> response = webClient
            .get()
            .uri(String.format("http://localhost:%s/api/accountandcustomer/102", port))
            .exchange();

    AccountWithCustomer accountWithCustomer = response.flatMap(resp -> {
      if (resp.statusCode() == HttpStatus.OK) {
        return resp.bodyToMono(AccountWithCustomer.class);
      }
      return Mono.error(new UnknownProxyException("Unknown: " + resp.statusCode()));
    }).block();


    assertEquals("Fred", accountWithCustomer.getCustomerInfo().getName().getFirstName());

    verify(getRequestedFor(urlMatching("/api/customers/101")));
    verify(getRequestedFor(urlMatching("/api/accounts/102")));
  }

  private JSONObject makeCustomerJSon() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("customerId", 101);

    JSONObject name = new JSONObject();
    name.put("firstName", "Fred");
    name.put("lastName", "George");
    json.put("name", name);

    json.put("creditLimit", "12.34");
    return json;
  }

  private JSONObject makeAccountJSon() throws JSONException {
    JSONObject json = new JSONObject();

    JSONObject accountInfo = new JSONObject();

    accountInfo.put("customerId", 101);

    json.put("accountInfo", accountInfo);

    return json;
  }

}