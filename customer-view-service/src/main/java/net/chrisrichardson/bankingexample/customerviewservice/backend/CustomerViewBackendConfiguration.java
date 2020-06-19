package net.chrisrichardson.bankingexample.customerviewservice.backend;

import io.eventuate.tram.events.common.DomainEventNameMapping;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class CustomerViewBackendConfiguration {

  @Bean
  public CustomerViewService customerViewService(MongoTemplate mongoTemplate, CustomerViewRepository customerViewRepository) {
    return new CustomerViewService(mongoTemplate, customerViewRepository);
  }


  @Bean
  public CustomerViewEventsSubscriber customerViewAccountEventsSubscriber(CustomerViewService customerViewService) {
    return new CustomerViewEventsSubscriber(customerViewService);
  }


  @Bean
  public DomainEventDispatcher customerViewAccountEventsSubscriberDispatcher(CustomerViewEventsSubscriber customerViewEventsSubscriber,
                                                                             MessageConsumer messageConsumer,
                                                                             DomainEventNameMapping domainEventNameMapping) {
    return new DomainEventDispatcher("customerViewEventsSubscriberDispatcher",
            customerViewEventsSubscriber.domainEventHandlers(),
            messageConsumer,
            domainEventNameMapping);
  }

}
