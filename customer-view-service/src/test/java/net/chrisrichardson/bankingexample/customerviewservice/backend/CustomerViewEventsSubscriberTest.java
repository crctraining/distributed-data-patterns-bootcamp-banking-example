package net.chrisrichardson.bankingexample.customerviewservice.backend;

import net.chrisrichardson.bankingexample.accountservice.common.AccountInfo;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountCreditedEvent;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountDebitedEvent;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountOpenedEvent;
import net.chrisrichardson.bankingexample.commondomain.Money;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given;

public class CustomerViewEventsSubscriberTest {

  private CustomerViewService customerViewService;
  private CustomerViewEventsSubscriber customerViewEventsSubscriber;
  private final long accountId = 101;
  private final String accountIdS = Long.toString(accountId);

  private long customerId = 102L;
  private String customerIdS = Long.toString(customerId);

  private final Money openingBalance = new Money("4.56");
  private final Money debitAmount = new Money(1);
  private final Money postDebitBalance = openingBalance.subtract(debitAmount);
  private final Money creditAmount = new Money(2);
  private final Money postCreditBalance = postDebitBalance.add(creditAmount);

  private AccountInfo accountInfo = new AccountInfo(customerId, "Checking", openingBalance);

  @Before
  public void setUp() {
    customerViewService = mock(CustomerViewService.class);
    customerViewEventsSubscriber = new CustomerViewEventsSubscriber(customerViewService);
  }

  @Test
  public void shouldHandleAccountOpenedEvent() {
    given().
            eventHandlers(customerViewEventsSubscriber.domainEventHandlers()).
            when().
            aggregate("net.chrisrichardson.bankingexample.accountservice.backend.Account", accountId).
            publishes(new AccountOpenedEvent(accountInfo)).
            then().
            verify(() -> {
              verify(customerViewService).openAccount(any(), eq(accountIdS), ArgumentMatchers.eq(accountInfo));
            })
    ;

  }

  @Test
  public void shouldHandleAccountDebitedEvent() {
    given().
            eventHandlers(customerViewEventsSubscriber.domainEventHandlers()).
            when().
            aggregate("net.chrisrichardson.bankingexample.accountservice.backend.Account", accountId).
            publishes(new AccountDebitedEvent(customerId, debitAmount, postDebitBalance, null)).
            then().
            verify(() -> {
              verify(customerViewService).debitAccount(any(), eq(accountIdS), eq(customerIdS), eq(debitAmount), eq(postDebitBalance), eq(null));
            })
    ;

  }

  @Test
  public void shouldHandleAccountCreditedEvent() {
    given().
            eventHandlers(customerViewEventsSubscriber.domainEventHandlers()).
            when().
            aggregate("net.chrisrichardson.bankingexample.accountservice.backend.Account", accountId).
            publishes(new AccountCreditedEvent(customerId, creditAmount, postCreditBalance, null)).
            then().
            verify(() -> {
              verify(customerViewService).creditAccount(any(), eq(accountIdS), eq(customerIdS), eq(creditAmount), eq(postCreditBalance), eq(null));
            })
    ;

  }


}