package net.chrisrichardson.bankingexample.endtoendtest;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.apigateway.AccountWithCustomer;
import io.eventuate.util.test.async.Eventually;
import io.eventuate.util.test.async.UrlTesting;
import net.chrisrichardson.bankingexample.accountservice.common.AccountInfo;
import net.chrisrichardson.bankingexample.accountservice.common.AccountState;
import net.chrisrichardson.bankingexample.accountservice.common.CreateAccountResponse;
import net.chrisrichardson.bankingexample.accountservice.common.GetAccountResponse;
import net.chrisrichardson.bankingexample.commondomain.Money;
import net.chrisrichardson.bankingexample.customerservice.common.Address;
import net.chrisrichardson.bankingexample.customerservice.common.CreateCustomerResponse;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
import net.chrisrichardson.bankingexample.customerservice.common.Name;
import net.chrisrichardson.bankingexample.customerviewservice.common.AccountView;
import net.chrisrichardson.bankingexample.customerviewservice.common.CustomerView;
import net.chrisrichardson.bankingexample.moneytransferservice.common.CreateMoneyTransferResponse;
import net.chrisrichardson.bankingexample.moneytransferservice.common.GetMoneyTransferResponse;
import net.chrisrichardson.bankingexample.moneytransferservice.common.MoneyTransferInfo;
import net.chrisrichardson.bankingexample.moneytransferservice.common.MoneyTransferState;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EndToEndTest {

  private RestTemplate restTemplate = new RestTemplate();
  private long customerId;
  private long fromAccountId;
  private long toAccountId;
  private long moneyTransferId;

  private static final String apiGatewayUrl = Optional.ofNullable(System.getenv("DOCKER_HOST_IP")).orElse("localhost");

  private static String baseUrl(String prefix, String... path) {
    StringBuilder sb = new StringBuilder("http://");
    sb.append(apiGatewayUrl);
    sb.append(":8080/api");
    sb.append(prefix);
    for (String x : path) {
      sb.append("/").append(x);
    }
    return sb.toString();
  }

  private String customerUrl(String... path) {
    return baseUrl("/customers", path);
  }

  private String customerViewUrl(String... path) {
    return baseUrl("/customerview", path);
  }

  private String moneyTransferUrl(String... path) {
    return baseUrl("/moneytransfers", path);
  }

  private String accountUrl(String... path) {
    return baseUrl("/accounts", path);
  }

  private String accountWithCustomerUrl(String... path) {
    return baseUrl("/accountandcustomer", path);
  }

  private String accountGroupsUrl(String... path) {
    return baseUrl("/accountgroups", path);
  }

  private Money fromInitialBalance = new Money("12.34");
  private Money toInitialBalance = new Money("100.86");
  private Money transferAmount = new Money("1.24");
  private Money fromFinalBalance = new Money("12.34").subtract(transferAmount);
  private Money toFinalBalance = new Money("100.86").add(transferAmount);


  @Test
  public void shouldCreateCustomer() {
    customerId = createCustomer();
  }

  @Test
  public void shouldCreateAccounts() {
    customerId = createCustomer();
    createAccounts();
  }

  private void createAccounts() {
    fromAccountId = createAccount(customerId, fromInitialBalance, "checking");

    assertAccountOpen(fromAccountId);

    assertEquals(fromInitialBalance, getBalance(fromAccountId));

    toAccountId = createAccount(customerId, toInitialBalance, "saving");

    assertAccountOpen(toAccountId);
    assertEquals(toInitialBalance, getBalance(toAccountId));
  }

  @Test
  public void shouldTransferMoney() {
    customerId = createCustomer();
    createAccounts();
    transferMoney();
  }

  private void transferMoney() {
    moneyTransferId = createMoneyTransfer(fromAccountId, toAccountId, transferAmount);
    assertTransferCompleted(moneyTransferId);
    assertEquals(fromFinalBalance, getBalance(fromAccountId));
    assertEquals(toFinalBalance, getBalance(toAccountId));
  }

  @Test
  public void shouldUpdateCustomerView() {
    customerId = createCustomer();
    createAccounts();
    transferMoney();
    assertCustomerViewUpdated(customerId, fromAccountId, fromFinalBalance, toAccountId, toFinalBalance, moneyTransferId);
  }

  @Test
  public void shouldReturnAccountWithCustomer() {
    customerId = createCustomer();
    createAccounts();
    AccountWithCustomer accountWithCustomer = getAccountWithCustomer(fromAccountId);
    // TODO = assert something
  }

  private AccountWithCustomer getAccountWithCustomer(long accountId) {
    ResponseEntity<AccountWithCustomer> response = restTemplate.getForEntity(accountWithCustomerUrl(Long.toString(accountId)),
            AccountWithCustomer.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }


  private void assertAccountOpen(long accountId) {
    Eventually.eventually(() -> {
      assertEquals(AccountState.OPEN, getAccount(accountId).getState());
    });

  }

  private void assertTransferCompleted(long moneyTransferId) {
    Eventually.eventually(() -> {
      ResponseEntity<GetMoneyTransferResponse> response = restTemplate.getForEntity(moneyTransferUrl(Long.toString(moneyTransferId)),
              GetMoneyTransferResponse.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals(MoneyTransferState.COMPLETED, response.getBody().getState());

    });
  }

  private long createMoneyTransfer(long fromAccountId, long toAccountId, Money transferAmount) {
    MoneyTransferInfo moneyTransferInfo = new MoneyTransferInfo(fromAccountId, toAccountId, transferAmount);
    ResponseEntity<CreateMoneyTransferResponse> createMoneyTransferResponse = restTemplate.postForEntity(moneyTransferUrl(),
            moneyTransferInfo,
            CreateMoneyTransferResponse.class);
    assertEquals(HttpStatus.OK, createMoneyTransferResponse.getStatusCode());
    CreateMoneyTransferResponse moneyTransferResponse = createMoneyTransferResponse.getBody();
    return moneyTransferResponse.getId();
  }

  private long createCustomer() {

    CustomerInfo customerInfo = new CustomerInfo(
            new Name("John", "Doe"), "510-555-1212",
            new Address("1 high street", null, "Oakland", "CA", "94719"),
            "xxx-yy-zzz");
    ResponseEntity<CreateCustomerResponse> createResponse = restTemplate.postForEntity(customerUrl(), customerInfo, CreateCustomerResponse.class);
    assertEquals(HttpStatus.OK, createResponse.getStatusCode());
    CreateCustomerResponse customerResponse = createResponse.getBody();

    return customerResponse.getId();
  }

  private long createAccount(long customerId, Money fromInitialBalance, String fromAccountTitle) {
    ResponseEntity<CreateAccountResponse> createAccount1 = restTemplate.postForEntity(accountUrl(),
            new AccountInfo(customerId, fromAccountTitle, fromInitialBalance),
            CreateAccountResponse.class);
    assertEquals(HttpStatus.OK, createAccount1.getStatusCode());
    CreateAccountResponse accountBody1 = createAccount1.getBody();
    return accountBody1.getId();
  }

  private Money getBalance(long accountId) {
    AccountInfo accountInfo = getAccount(accountId).getAccountInfo();
    return accountInfo.getBalance();
  }

  private GetAccountResponse getAccount(long accountId) {
    ResponseEntity<GetAccountResponse> response = restTemplate.getForEntity(accountUrl(Long.toString(accountId)),
            GetAccountResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }


  private void assertCustomerViewUpdated(long customerId, long fromAccountId, Money fromExpectedBalance,
                                         long toAccountId, Money toExpectedBalance, long moneyTransferId) {
    Eventually.eventually(() -> {
      ResponseEntity<CustomerView> response = restTemplate.getForEntity(customerViewUrl(Long.toString(customerId)), CustomerView.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      CustomerView customerView = response.getBody();

      AccountView fromAccount = customerView.getAccounts().get(Long.toString(fromAccountId));
      assertNotNull(fromAccount);
      assertEquals(fromExpectedBalance, fromAccount.getBalance());

      AccountView toAccount = customerView.getAccounts().get(Long.toString(toAccountId));
      assertNotNull(toAccount);
      assertEquals(toExpectedBalance, toAccount.getBalance());
    });
  }

  @Test
  public void testSwaggerUiUrls() throws IOException {
    testSwaggerUiUrl(8080);
    testSwaggerUiUrl(8081);
    testSwaggerUiUrl(8082);
    testSwaggerUiUrl(8083);
    testSwaggerUiUrl(8084);
  }

  private void testSwaggerUiUrl(int port) throws IOException {
    UrlTesting.assertUrlStatusIsOk("localhost", port, "/v3/api-docs");
    UrlTesting.assertUrlStatusIsOk("localhost", port, "/swagger-ui/index.html");
    UrlTesting.assertUrlStatusIsOk("localhost", port, "/swagger-ui.html");
  }



}
