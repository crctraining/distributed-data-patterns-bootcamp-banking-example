package net.chrisrichardson.bankingexample.customerviewservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import net.chrisrichardson.bankingexample.customerviewservice.web.CustomerViewWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CustomerViewWebConfiguration.class,
        TramJdbcKafkaConfiguration.class})
@EnableAutoConfiguration
public class CustomerViewServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(CustomerViewServiceMain.class, args);
  }
}
