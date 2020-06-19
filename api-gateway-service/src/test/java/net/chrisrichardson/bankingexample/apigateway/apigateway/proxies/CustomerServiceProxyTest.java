package net.chrisrichardson.bankingexample.apigateway.apigateway.proxies;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomerServiceProxyTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
        "api.gateway.destinations.customerServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.accountServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.moneyTransferServiceUrl=http://localhost:${wiremock.server.port}",
                "api.gateway.destinations.customerViewServiceUrl=http://localhost:${wiremock.server.port}"
})
@AutoConfigureWireMock(port = 0)
public class CustomerServiceProxyTest {

  @Configuration
  @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
  @Import(ProxyConfiguration.class)
  static public class Config {
  }

  @Autowired
  private CustomerServiceProxy customerServiceProxy;

  @Test
  public void shouldCallCustomerService() throws JSONException {

    JSONObject json = new JSONObject();
    json.put("customerId", 101);

    JSONObject name = new JSONObject();
    name.put("firstName", "Fred");
    name.put("lastName", "George");
    json.put("name", name);

    json.put("creditLimit", "12.34");

    String expectedResponse = json.toString();

    stubFor(get(urlEqualTo("/api/customers/101"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(expectedResponse)));


    CustomerInfo customer = customerServiceProxy.findCustomerById(101).block();

    assertEquals("Fred", customer.getName().getFirstName());

    verify(getRequestedFor(urlMatching("/api/customers/101")));
  }

  @Test(expected = CallNotPermittedException.class)
  public void shouldTimeoutAndTripCircuitBreaker() {

    String expectedResponse = "{}";

    stubFor(get(urlEqualTo("/customers/99"))
            .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody(expectedResponse)));


    IntStream.range(0, 100).forEach(i -> {
              try {
                customerServiceProxy.findCustomerById(99).block();
              } catch (CallNotPermittedException e) {
                throw e;
              } catch (UnknownProxyException e) {
                //
              }
            }
    );

    verify(getRequestedFor(urlMatching("/customers/99")));
  }
}