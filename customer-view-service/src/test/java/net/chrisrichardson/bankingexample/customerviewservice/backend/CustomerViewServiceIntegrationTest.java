package net.chrisrichardson.bankingexample.customerviewservice.backend;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.spring.jdbc.EventuateTransactionTemplateConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;

import net.chrisrichardson.bankingexample.accountservice.common.AccountInfo;
import net.chrisrichardson.bankingexample.commondomain.Money;
import net.chrisrichardson.bankingexample.customerservice.common.Address;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
import net.chrisrichardson.bankingexample.customerservice.common.Name;
import net.chrisrichardson.bankingexample.customerviewservice.common.AccountChange;
import net.chrisrichardson.bankingexample.customerviewservice.common.AccountChangeType;
import net.chrisrichardson.bankingexample.customerviewservice.common.AccountView;
import net.chrisrichardson.bankingexample.customerviewservice.common.CustomerView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomerViewServiceIntegrationTest.CustomerViewServiceIntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerViewServiceIntegrationTest {

  @Autowired
  private CustomerViewService customerViewService;

  private IdGenerator idGenerator = new ApplicationIdGenerator();
  private long customerId;
  private String customerIdS;
  private long accountId;
  private String accountIdS;
  private String createAccount1EventId;
  private String debitTransactionId;
  private String creditTransactionId;

  private final Money openingBalance = new Money("4.56");
  private final Money debitAmount = new Money(1);
  private final Money postDebitBalance = openingBalance.subtract(debitAmount);
  private final Money creditAmount = new Money(2);
  private final Money postCreditBalance = postDebitBalance.add(creditAmount);

  @Before
  public void setUp() {
    customerId = System.currentTimeMillis();
    customerIdS = Long.toString(customerId);
    accountId = System.currentTimeMillis();
    accountIdS = Long.toString(accountId);

    createAccount1EventId = idGenerator.genId(0L).asString();
    debitTransactionId = idGenerator.genId(0L).asString();
    creditTransactionId = idGenerator.genId(0L).asString();
  }

  @Test
  public void shouldDoSomething() {

    CustomerInfo customerInfo = makeCustomer();

    customerViewService.createCustomer(customerIdS, customerInfo);

    AccountInfo accountInfo1 = new AccountInfo(customerId, "Checking", openingBalance);

    customerViewService.openAccount(createAccount1EventId, accountIdS, accountInfo1);

    customerViewService.debitAccount(idGenerator.genId(0L).asString(), accountIdS, customerIdS, debitAmount, postDebitBalance, debitTransactionId);

    customerViewService.creditAccount(idGenerator.genId(0L).asString(), accountIdS, customerIdS, creditAmount, postCreditBalance, creditTransactionId);

    Optional<CustomerView> maybeCustomer = customerViewService.findByCustomerId(customerIdS);
    assertTrue(maybeCustomer.isPresent());
    CustomerView customer = maybeCustomer.get();

    assertEquals(customerInfo.getSsn(), customer.getCustomerInfo().getSsn());
    assertEquals(1, customer.getAccounts().size());

    Map.Entry<String, AccountView> accountIdAndAccount = customer.getAccounts().entrySet().iterator().next();
    assertEquals(accountIdS, accountIdAndAccount.getKey());

    AccountView account = accountIdAndAccount.getValue();
    List<AccountChange> accountChanges = account.getChanges().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.toList());

    assertEquals(3, accountChanges.size());

    AccountChange change1 = accountChanges.get(0);
    assertEquals(AccountChangeType.OPEN, change1.getType());
    AccountChange change2 = accountChanges.get(1);
    assertEquals(AccountChangeType.DEBIT, change2.getType());
    AccountChange change3 = accountChanges.get(2);
    assertEquals(AccountChangeType.CREDIT, change3.getType());
  }

  private CustomerInfo makeCustomer() {
    return new CustomerInfo(
            new Name("John", "Doe"), "510-555-1212",
            new Address("1 high street", null, "Oakland", "CA", "94719"),
            "xxx-yy-zzz");
  }

  @Test
  public void shouldOpenAccountBeforeCreatingCustomer() {

    AccountInfo accountInfo1 = new AccountInfo(customerId, "Checking", openingBalance);

    customerViewService.openAccount(createAccount1EventId, accountIdS, accountInfo1);

    CustomerInfo customerInfo = makeCustomer();

    customerViewService.createCustomer(customerIdS, customerInfo);

    customerViewService.debitAccount(idGenerator.genId(0L).asString(), accountIdS, customerIdS, debitAmount, postDebitBalance, debitTransactionId);

    Optional<CustomerView> customerView = customerViewService.findByCustomerId(customerIdS);
    assertTrue(customerView.isPresent());
    assertEquals(customerInfo.getSsn(), customerView.get().getCustomerInfo().getSsn());
  }

  @Configuration
  @Import({CustomerViewBackendConfiguration.class, TramInMemoryConfiguration.class, EventuateTransactionTemplateConfiguration.class})
  @EnableAutoConfiguration
  public static class CustomerViewServiceIntegrationTestConfiguration {
  }
}
