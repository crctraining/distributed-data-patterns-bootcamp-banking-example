package net.chrisrichardson.bankingexample.accountservice.messaging;

import io.eventuate.tram.events.common.DomainEventNameMapping;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import net.chrisrichardson.bankingexample.accountservice.backend.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountMessagingConfiguration {

  @Bean
  public AccountEventHandlers accountEventHandlers(AccountService accountService) {
    return new AccountEventHandlers(accountService);
  }

  @Bean
  public DomainEventDispatcher accountServiceDomainEventDispatcher(AccountEventHandlers orderHistoryEventHandlers,
                                                                   MessageConsumer messageConsumer,
                                                                   DomainEventNameMapping domainEventNameMapping) {
    return new DomainEventDispatcher("accountServiceDomainEventDispatcher",
            orderHistoryEventHandlers.domainEventHandlers(),
            messageConsumer,
            domainEventNameMapping);
  }

  @Bean
  public AccountCommandHandlers accountServiceCommandHandlers(AccountService accountService) {
    return new AccountCommandHandlers(accountService);
  }

  @Bean
  public SagaCommandDispatcher orderCommandHandlersDispatcher(AccountCommandHandlers accountServiceCommandHandlers,
                                                              SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
    return sagaCommandDispatcherFactory.make("accountServiceCommands",
            accountServiceCommandHandlers.commandHandlers());

  }


}
