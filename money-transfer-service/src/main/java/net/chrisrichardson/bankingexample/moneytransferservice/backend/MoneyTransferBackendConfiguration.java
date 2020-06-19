package net.chrisrichardson.bankingexample.moneytransferservice.backend;

import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.AccountServiceProxy;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.TransferMoneySaga;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EntityScan
@Import({SagaOrchestratorConfiguration.class, SagaParticipantConfiguration.class, TramEventsPublisherConfiguration.class})
public class MoneyTransferBackendConfiguration {

  @Bean
  public MoneyTransferService moneyTransferService(MoneyTransferRepository moneyTransferRepository,
                                                   SagaInstanceFactory sagaInstanceFactory,
                                                   TransferMoneySaga transferMoneySaga) {
    return new MoneyTransferService(moneyTransferRepository, sagaInstanceFactory, transferMoneySaga);
  }


//  @Bean
//  public MoneyTransferEventSubscriber moneyTransferEventSubscriber() {
//    return new MoneyTransferEventSubscriber();
//  }

  @Bean
  public TransferMoneySaga transferMoneySaga(MoneyTransferRepository moneyTransferRepository) {
    return new TransferMoneySaga(new AccountServiceProxy(), moneyTransferRepository);
  }

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

}
