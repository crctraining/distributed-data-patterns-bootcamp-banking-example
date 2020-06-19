package net.chrisrichardson.bankingexample.accountservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import net.chrisrichardson.bankingexample.accountservice.messaging.AccountMessagingConfiguration;
import net.chrisrichardson.bankingexample.accountservice.web.AccountWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({AccountWebConfiguration.class,
        AccountMessagingConfiguration.class,
        TramJdbcKafkaConfiguration.class,
})
public class AccountServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceMain.class, args);
    }
}
