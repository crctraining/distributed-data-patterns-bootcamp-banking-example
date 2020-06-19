package net.chrisrichardson.bankingexample.customerviewservice.backend;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountCreditedEvent;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountDebitedEvent;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountOpenedEvent;
import net.chrisrichardson.bankingexample.customerservice.common.CustomerCreatedEvent;

public class CustomerViewEventsSubscriber {

  private CustomerViewService customerViewService;

  public CustomerViewEventsSubscriber(CustomerViewService customerViewService) {
    this.customerViewService = customerViewService;
  }

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType("net.chrisrichardson.bankingexample.accountservice.backend.Account")
            .onEvent(AccountOpenedEvent.class, this::handleAccountOpenedEvent)
            .onEvent(AccountDebitedEvent.class, this::handleAccountDebitedEvent)
            .onEvent(AccountCreditedEvent.class, this::handleAccountCreditedEvent)
            .andForAggregateType("net.chrisrichardson.bankingexample.customerservice.backend.Customer")
            .onEvent(CustomerCreatedEvent.class, this::handleCustomerCreatedEvent)
            .build();
  }


  public void handleCustomerCreatedEvent(DomainEventEnvelope<CustomerCreatedEvent> dee) {
    customerViewService.createCustomer(dee.getAggregateId(), dee.getEvent().getCustomerInfo());

  }

  public void handleAccountOpenedEvent(DomainEventEnvelope<AccountOpenedEvent> dee) {
    customerViewService.openAccount(dee.getEventId(), dee.getAggregateId(), dee.getEvent().getAccountInfo());
  }

  public void handleAccountDebitedEvent(DomainEventEnvelope<AccountDebitedEvent> de) {
    AccountDebitedEvent event = de.getEvent();
    customerViewService.debitAccount(de.getEventId(), de.getAggregateId(), Long.toString(event.getCustomerId()),
            event.getAmount(),
            event.getNewBalance(), event.getTransactionId());
  }

  public void handleAccountCreditedEvent(DomainEventEnvelope<AccountCreditedEvent> de) {
    throw new RuntimeException("not yet implemented");
  }


}
