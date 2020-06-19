package net.chrisrichardson.bankingexample.customerservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import net.chrisrichardson.bankingexample.customerservice.web.CustomerWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CustomerWebConfiguration.class,
        TramJdbcKafkaConfiguration.class})
@EnableAutoConfiguration
public class CustomerServiceMain {

  public static void main(String[] args) {
    SpringApplication.run(CustomerServiceMain.class, args);
  }
}
