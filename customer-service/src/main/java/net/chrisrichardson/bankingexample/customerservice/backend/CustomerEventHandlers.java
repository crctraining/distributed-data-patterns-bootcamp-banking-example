package net.chrisrichardson.bankingexample.customerservice.backend;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import net.chrisrichardson.bankingexample.accountservice.common.events.AccountOpenedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerEventHandlers {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private CustomerService customerService;

  public CustomerEventHandlers(CustomerService customerService) {
    this.customerService = customerService;
  }

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder
            .forAggregateType("net.chrisrichardson.bankingexample.accountservice.backend.Account")
            .onEvent(AccountOpenedEvent.class, this::handleAccountOpenedEvent)
            .build();
  }

  private  void handleAccountOpenedEvent(DomainEventEnvelope<AccountOpenedEvent> dee) {
    logger.info("CustomerEventHandlers received AccountOpenedEvent = {}", dee);
    customerService.validateCustomer(Long.parseLong(dee.getAggregateId()), dee.getEvent().getAccountInfo().getCustomerId());
  }
}
