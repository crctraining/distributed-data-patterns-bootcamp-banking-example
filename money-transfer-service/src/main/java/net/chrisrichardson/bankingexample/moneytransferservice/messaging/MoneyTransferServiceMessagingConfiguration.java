package net.chrisrichardson.bankingexample.moneytransferservice.messaging;

import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferBackendConfiguration;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MoneyTransferBackendConfiguration.class})
public class MoneyTransferServiceMessagingConfiguration {

  @Bean
  public MoneyTransferServiceCommandHandlers moneyTransferServiceCommandHandlers(MoneyTransferService moneyTransferService) {
    return new MoneyTransferServiceCommandHandlers(moneyTransferService);
  }

  @Bean
  public SagaCommandDispatcher orderCommandHandlersDispatcher(MoneyTransferServiceCommandHandlers moneyTransferServiceCommandHandlers,
                                                              MessageConsumer messageConsumer,
                                                              MessageProducer messageProducer,
                                                              SagaLockManager sagaLockManager) {
    return new SagaCommandDispatcher("moneyTransferServiceCommands",
            moneyTransferServiceCommandHandlers.commandHandlers(),
            messageConsumer,
            messageProducer,
            sagaLockManager);
  }


}
