package net.chrisrichardson.bankingexample.moneytransferservice.webtest;

import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;
import net.chrisrichardson.bankingexample.moneytransferservice.web.MoneyTransferWebConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@Import({MoneyTransferWebConfiguration.class, TramSagaInMemoryConfiguration.class})
@EnableAutoConfiguration
public class MoneyTransferServiceRestApiIntegrationTestConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
